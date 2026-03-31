package com.brunomoura.ecommerceapi.domain.user;

import com.brunomoura.ecommerceapi.enums.UserRole;
import com.brunomoura.ecommerceapi.exception.user.AddressAlreadyExistsException;
import com.brunomoura.ecommerceapi.exception.user.AddressNotFoundException;
import com.brunomoura.ecommerceapi.exception.user.InvalidUserException;
import com.brunomoura.ecommerceapi.exception.user.MissingRequiredAddressException;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    //region FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Column(length = 11, unique = true)
    private String cpf;

    @Column(unique = true)
    private String email;

    @Column(length = 14)
    private String phoneNumber;

    private LocalDate dateOfBirth;

    @Column(length = 128)
    private String passwordHash;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    private boolean active;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Address> addresses = new HashSet<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Long version;
    //endregion

    //region CONSTRUCTORS
    protected User() {
    }

    public User(String name, String cpf, String email, String phoneNumber, LocalDate dateOfBirth,
                String passwordHash) {

        validateStringField("name", name);
        validateStringField("cpf", cpf);
        validateStringField("email", email);
        validateStringField("phoneNumber", phoneNumber);
        validateDateOfBirth(dateOfBirth);
        validateStringField("passwordHash", passwordHash);

        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.passwordHash = passwordHash;
        this.role = UserRole.USER;
        this.active = true;
    }
    //endregion

    //region GETTERS
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
    //endregion

    //region DOMAIN METHODS

    // Ensures address uniqueness within the aggregate based on value equality.
    public void addAddress(String label, String streetName, String houseNumber, String neighborhood, String state,
                           String country, String cep) {

        Optional<Address> foundAddress = findSameAddress(streetName, houseNumber, neighborhood, state, country, cep);

        if (foundAddress.isPresent()) {
            throw new AddressAlreadyExistsException("Address already exists.");
        }

        addAddressInternal(label, streetName, houseNumber, neighborhood, state, country, cep);
    }


    public void removeAddress(Long id) {
        Address foundAddress = findAddress(id);

        // Prevents address must contain one address at least
        if (this.addresses.size() < 2) {
            throw new MissingRequiredAddressException("Addresses must contain one address at least.");
        }

        this.addresses.remove(foundAddress);
    }

    // Prevents duplicate addresses and ensures idempotent updates.
    public void updateAddress(Long id, String label, String streetName, String houseNumber, String neighborhood,
                              String state, String country, String cep) {
        Optional<Address> foundAddress = findSameAddress(streetName, houseNumber, neighborhood, state, country, cep);

        if (foundAddress.isPresent()) {

            if (foundAddress.get().getId().equals(id)) {
                // No-op: same address, no state change required
                return;
            } else {
                throw new AddressAlreadyExistsException("Address already exists.");
            }
        } else {
            // Add first to ensure the user is never left without an address.
            // This avoids violating the "at least one address" business rule.
            addAddress(label, streetName, houseNumber, neighborhood, state, country, cep);
            removeAddress(id);
        }
    }
    //endregion

    //region INTERNAL METHODS

    // Searches for an address using value-based equality. (not by ID)
    // Using to enforce uniqueness within the aggregate.
    private Optional<Address> findSameAddress(String streetName, String houseNumber, String neighborhood, String state,
                                              String country, String cep) {
        return this.addresses.stream().filter(address -> address.isSameAddress(
                streetName, houseNumber, neighborhood, state, country, cep)).findFirst();
    }

    private Address findAddress(Long id) {
        Optional<Address> findAddress = this.addresses.stream().filter(address -> address.getId().equals(id))
                .findFirst();

        if (findAddress.isEmpty()) {
            throw new AddressNotFoundException("Address not found.");
        }

        return findAddress.get();
    }

    private void addAddressInternal(String label, String streetName, String houseNumber, String neighborhood, String state,
                                    String country, String cep) {
        Address newAddress = new Address(label, streetName, houseNumber, neighborhood, state, country, cep);
        newAddress.setUser(this);
        this.addresses.add(newAddress);
    }

    private void validateStringField(String field, String value) {
        if (value == null) {
            throw new InvalidUserException(String.format("Invalid user. Field: %s cannot be null or blank.", field));
        }

        if (value.isBlank()) {
            throw new InvalidUserException(String.format("Invalid user. Field: %s cannot be null or blank.", field));
        }
    }

    private void validateDateOfBirth(LocalDate dateValue) {
        if (dateValue == null) {
            throw new InvalidUserException("Invalid user. Field: dateOfBirth cannot be null or blank.");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate maxBirthDate = currentDate.minusYears(125);
        LocalDate minBirthDate = currentDate.minusYears(18);

        if (dateValue.isAfter(currentDate)) {
            throw new InvalidUserException("Invalid user. Field: dateOfBirth cannot be in the future.");
        }

        if (dateValue.isBefore(maxBirthDate)) {
            throw new InvalidUserException(
                    "Invalid user. Field: dateOfBirth violates the maximum age constraint (125 years).");
        }

        if (dateValue.isAfter(minBirthDate)) {
            throw new InvalidUserException(
                    "Invalid user. Field: dateOfBirth violates the minimum age constraint (18 years).");
        }
    }
    //endregion

}
