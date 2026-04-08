package com.brunomoura.ecommerceapi.domain.user;

import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.enums.UserRole;

import com.brunomoura.ecommerceapi.exception.BusinessException;
import com.brunomoura.ecommerceapi.exception.NotFoundException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @Column(length = 100)
    private String name;

    @Getter @Setter
    @Column(length = 11, unique = true)
    private String cpf;

    @Getter
    @Column(unique = true)
    private String email;

    @Getter @Setter
    @Column(length = 14)
    private String phoneNumber;

    @Getter @Setter
    private LocalDate dateOfBirth;

    @Getter
    @Column(length = 128)
    private String passwordHash;

    @Getter
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Getter
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Address> addresses = new HashSet<>();

    @Getter
    private Instant deletedAt;

    @Getter
    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Long version;

    protected User() {
    }

    public User(String name, String email, String cpf, String phoneNumber, LocalDate dateOfBirth,
                String passwordHash) {

        validateStringField("name", name);
        validateStringField("cpf", cpf);
        validateStringField("email", email);
        validateStringField("phoneNumber", phoneNumber);
        validateDateOfBirth(dateOfBirth);
        validateStringField("passwordHash", passwordHash);
        validatePasswordAgainstEmail(passwordHash);

        this.name = name;
        this.cpf = cpf;
        this.email = email.toLowerCase();
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.passwordHash = passwordHash;
        this.role = UserRole.USER;
        this.deletedAt = null;
    }

    public void setEmail(String email) {

        this.email = email.toLowerCase();
    }

    public void changePassword(String newPassword, String newPasswordHash) {

        validatePasswordAgainstEmail(newPassword);

        this.passwordHash = newPasswordHash;
    }

    public boolean isActive() {

        return this.deletedAt == null;
    }

    public void activeUser() {
        if (this.deletedAt != null) {
            setDeletedAt(null);
        }
    }

    public void deleteUser() {
        if (this.deletedAt == null) {
            setDeletedAt(Instant.now());
        }
    }

    // Ensures address uniqueness within the aggregate based on value equality.
    public Address addAddress(String label, String streetName, String houseNumber, String neighborhood, String state,
                              String country, String cep) {
        ensuresUserActive();

        Address newAddress = new Address(label, streetName, houseNumber, neighborhood, state, country, cep);

        Optional<Address> foundAddress = findSameAddress(newAddress);

        if (foundAddress.isPresent()) {
            throw new BusinessException(ErrorCode.ADDRESS_ALREADY_EXISTS, "Address already exists.");
        }

        return addAddressInternal(newAddress);
    }

    public void removeAddress(Long id) {
        ensuresUserActive();

        Address foundAddress = findAddress(id);

        // Prevents address must contain one address at least
        if (this.addresses.size() < 2) {
            throw new BusinessException(ErrorCode.LAST_ADDRESS_REMOVAL_NOT_ALLOWED,
                    "Addresses must contain one address at least.");
        }

        this.addresses.remove(foundAddress);
    }

    // Prevents duplicate addresses and ensures idempotent updates.
    public Address updateAddress(Long id, String label, String streetName, String houseNumber, String neighborhood,
                                 String state, String country, String cep) {
        ensuresUserActive();

        Address address = new Address(
                label, streetName, houseNumber, neighborhood, state, country, cep);

        Optional<Address> foundAddress = findSameAddress(address);

        if (foundAddress.isPresent()) {

            if (foundAddress.get().getId().equals(id)) {
                // No-op: same address, no state change required
                 return foundAddress.get();
            } else {
                throw new BusinessException(ErrorCode.ADDRESS_ALREADY_EXISTS ,"Address already exists.");
            }
        } else {
            // Add first to ensure the user is never left without an address.
            // This avoids violating the "at least one address" business rule.
            Address newAddress = addAddress(label, streetName, houseNumber, neighborhood, state, country, cep);
            removeAddress(id);

            return newAddress;
        }
    }

    // Searches for an address using value-based equality. (not by ID)
    // Using to enforce uniqueness within the aggregate.
    private Optional<Address> findSameAddress(Address otherAddress) {
        return this.addresses.stream().filter(address -> address.isSameAddress(otherAddress)).findFirst();
    }

    private Address findAddress(Long id) {
        Optional<Address> findAddress = this.addresses.stream().filter(address -> address.getId().equals(id))
                .findFirst();

        if (findAddress.isEmpty()) {
            throw new NotFoundException(ErrorCode.ADDRESS_NOT_FOUND,"Address not found.");
        }

        return findAddress.get();
    }

    private Address addAddressInternal(Address address) {
        address.setUser(this);
        this.addresses.add(address);

        return address;
    }

    private void validateStringField(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_USER_FIELD,
                    String.format("Invalid user. Field: %s cannot be null or blank.", field));
        }
    }

    private void validateDateOfBirth(LocalDate dateValue) {
        if (dateValue == null) {
            throw new BusinessException(ErrorCode.INVALID_USER_FIELD,
                    "Invalid user. Field: dateOfBirth cannot be null or blank.");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate maxBirthDate = currentDate.minusYears(125);
        LocalDate minBirthDate = currentDate.minusYears(18);

        if (dateValue.isAfter(currentDate)) {
            throw new BusinessException(ErrorCode.INVALID_RANGE_DATE ,"Invalid user. " +
                    "Field: dateOfBirth cannot be in the future.");
        }

        if (dateValue.isBefore(maxBirthDate)) {
            throw new BusinessException(ErrorCode.INVALID_RANGE_DATE,
                    "Invalid user. Field: dateOfBirth violates the maximum age constraint (125 years).");
        }

        if (dateValue.isAfter(minBirthDate)) {
            throw new BusinessException(ErrorCode.INVALID_RANGE_DATE,
                    "Invalid user. Field: dateOfBirth violates the minimum age constraint (18 years).");
        }
    }

    private void validatePasswordAgainstEmail(String password) {

        if (Objects.equals(password.toLowerCase(), this.email)) {
            throw new BusinessException(ErrorCode.WEAK_PASSWORD,
                    "Weak password. The provided password does not meet the minimum security requirements.");
        }
    }

    private void ensuresUserActive() {

        if (this.deletedAt != null) {
            throw new BusinessException(ErrorCode.USER_DELETED_CANNOT_BE_CHANGED,
                    "Users deleted cannot be changed.");
        }
    }

    private void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

}
