package com.brunomoura.ecommerceapi.domain.user;

import com.brunomoura.ecommerceapi.enums.UserRole;
import com.brunomoura.ecommerceapi.exception.user.InvalidUserException;
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

    private String name;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private LocalDate dateOfBirth;

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
    public User() {
    }

    public User(String name, String cpf, String email, String phoneNumber, LocalDate dateOfBirth,
                String passwordHash) {

        validateField("name", name);
        validateField("cpf", cpf);
        validateField("email", email);
        validateField("phoneNumber", phoneNumber);
        validateField("dateOfBirth", dateOfBirth);
        validateField("passwordHash", passwordHash);

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
    public void addAddress(String label, String streetName, String houseNumber, String neighborhood, String state,
                           String country, String cep) {

        Address newAddress = new Address(label, streetName, houseNumber, neighborhood, state, country, cep);

        Optional<Address> existingAddress = findSameAddress(newAddress);

        existingAddress.ifPresentOrElse(Address::activate, () -> addAddressInternal(newAddress));
    }

    //endregion

    //region INTERNAL METHODS
    private Optional<Address> findSameAddress(Address newAddress) {
        return this.addresses.stream().filter(address -> address.isSameAddress(newAddress)).findFirst();
    }

    private void addAddressInternal(Address newAddress) {
        newAddress.setUser(this);
        this.addresses.add(newAddress);
    }

    private <T> void validateField(String field, T value) {
        LocalDate maxBirthDate = LocalDate.now().minusYears(125);
        LocalDate minBirthDate = LocalDate.now().minusYears(18);

        if (value == null) {
            throw new InvalidUserException(String.format("Invalid user. Field: %s cannot be null or blank.", field));
        }

        if (value instanceof String stringValue) {
            if (stringValue.isBlank()) {
                throw new InvalidUserException(String.format("Invalid user. Field: %s cannot be null or blank.", field));
            }
        }

        if (value instanceof LocalDate dateValue) {

            if (dateValue.isAfter(LocalDate.now())) {
                throw new InvalidUserException(String.format("Invalid user. Field: %s cannot be in the future.", field));
            }
            if (dateValue.isBefore(maxBirthDate)) {
                throw new InvalidUserException(String.format(
                        "Invalid user. Field: %s violates the maximum age constraint (125 years).", field));
            }
            if (dateValue.isAfter(minBirthDate)) {
                throw new InvalidUserException(String.format(
                        "Invalid user. Field: %s violates the minimum age constraint (18 years).", field));
            }
        }
    }
    //endregion

}
