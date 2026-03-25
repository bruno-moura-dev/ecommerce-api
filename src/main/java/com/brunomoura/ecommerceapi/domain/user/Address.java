package com.brunomoura.ecommerceapi.domain.user;

import com.brunomoura.ecommerceapi.exception.user.InvalidAddressException;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "user_addresses")
public class Address {

    //region FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    private String streetName;

    private String houseNumber;

    private String neighborhood;

    private String state;

    private String country;

    private String cep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean active;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Long version;
    //endregion

    //region CONSTRUCTORS
    protected Address() {}

     Address(String label, String streetName, String houseNumber, String neighborhood, String state, String country,
             String cep) {

        validateField("streetName", streetName);
        validateField("houseNumber", houseNumber);
        validateField("neighborhood", neighborhood);
        validateField("state", state);
        validateField("country", country);
        validateField("cep", cep);

        this.label = label;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.neighborhood = neighborhood;
        this.state = state;
        this.country = country;
        this.cep = cep;
        this.active = true;
    }

    //endregion

    //region GETTERS
    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getCep() {
        return cep;
    }

    public User getUser() {
        return user;
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

    //region SETTERS
    void setUser(User user) {
        this.user = user;
    }
    //endregion

    //region DOMAIN METHODS
    public boolean isSameAddress(Address otherAddress) {
        return Objects.equals(this.streetName, otherAddress.getStreetName())
                && Objects.equals(this.houseNumber, otherAddress.getHouseNumber())
                && Objects.equals(this.neighborhood, otherAddress.getNeighborhood())
                && Objects.equals(this.state, otherAddress.getState())
                && Objects.equals(this.country, otherAddress.getCountry())
                && Objects.equals(this.cep, otherAddress.getCep());
    }

    public void activate() {
        if (!this.active) {
            this.active = true;
        }
    }
    //endregion

    //region INTERNAL METHODS
    private void validateField(String field ,String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidAddressException(String.format("Invalid address. Field: %s", field));
        }
    }
    //endregion

    //region INFRASTRUCTURE METHODS
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return isSameAddress(address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.streetName, this.houseNumber, this.neighborhood, this.state, this.country,
                this.cep);
    }
    //endregion
}
