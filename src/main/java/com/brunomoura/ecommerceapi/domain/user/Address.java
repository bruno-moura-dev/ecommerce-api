package com.brunomoura.ecommerceapi.domain.user;

import com.brunomoura.ecommerceapi.exception.user.InvalidAddressException;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "user_addresses")
@Getter
public class Address {

    //region FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String label;

    @Column(length = 100)
    private String streetName;

    @Column(length = 10)
    private String houseNumber;

    @Column(length = 60)
    private String neighborhood;

    @Column(length = 50)
    private String state;

    @Column(length = 60)
    private String country;

    @Column(length = 8)
    private String cep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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
    }
    //endregion

    //region SETTERS
    void setUser(User user) {
        this.user = user;
    }
    //endregion

    //region DOMAIN METHODS
    boolean isSameAddress(String streetName, String houseNumber, String neighborhood, String state,
                                 String country, String cep) {
        return Objects.equals(this.streetName, streetName)
                && Objects.equals(this.houseNumber, houseNumber)
                && Objects.equals(this.neighborhood, neighborhood)
                && Objects.equals(this.state, state)
                && Objects.equals(this.country, country)
                && Objects.equals(this.cep, cep);
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
    // Equality is based on address fields (value-based), not on ID.
    // This allows the Set to prevent duplicate addresses.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return isSameAddress(address.getStreetName(), address.getHouseNumber(), address.getNeighborhood(),
                address.getState(), address.getCountry(), address.getCep());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.streetName, this.houseNumber, this.neighborhood, this.state, this.country,
                this.cep);
    }
    //endregion
}
