package com.brunomoura.ecommerceapi.domain.user;

import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.exception.base.BusinessException;
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
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 60)
    private String country;

    @Column(length = 8)
    private String zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Long version;

    protected Address() {}

     Address(String label, String streetName, String houseNumber, String neighborhood, String city, String state,
             String country, String zipCode) {

        validateField("streetName", streetName);
        validateField("city", city);
        validateField("state", state);
        validateField("country", country);

        this.label = label;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }

    void setUser(User user) {
        this.user = user;
    }

    Address update(AddressUpdate address) {

        if (address.getLabel() != null) {this.label = address.getLabel();}
        if (address.getStreetName() != null) {this.streetName = address.getStreetName();}
        if (address.getHouseNumber() != null) {this.houseNumber = address.getHouseNumber();}
        if (address.getNeighborhood() != null) {this.neighborhood = address.getNeighborhood();}
        if (address.getCity() != null) {this.city = address.getCity();}
        if (address.getState() != null) {this.state = address.getState();}
        if (address.getCountry() != null) {this.country = address.getCountry();}
        if (address.getZipCode() != null) {this.zipCode = address.getZipCode();}

        return this;
    }

    boolean isSameAddress(Address address) {
        return Objects.equals(this.streetName, address.getStreetName())
                && Objects.equals(this.houseNumber, address.getHouseNumber())
                && Objects.equals(this.neighborhood, address.getNeighborhood())
                && Objects.equals(this.city, address.getCity())
                && Objects.equals(this.state, address.getState())
                && Objects.equals(this.country, address.getCountry())
                && Objects.equals(this.zipCode, address.getZipCode());
    }

    private void validateField(String field ,String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_ADDRESS_FIELD, "Field must not be null or blank: field="
                    + field);
        }
    }
}
