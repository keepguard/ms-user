package com.keepguard.ms_user.domain.entity;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.AddressTypeEnum;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Address {

    private final UUID id;
    private final UUID userId;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private AddressTypeEnum type;
    private boolean primary;
    private boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Address(UUID id, UUID userId, String street, String number, String complement,
                   String neighborhood, String city, String state, String zipCode, String country,
                   AddressTypeEnum type, boolean primary, boolean active,
                   OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.userId = Objects.requireNonNull(userId, "userId é obrigatório");
        this.street = validateStreet(street);
        this.number = validateNumber(number);
        this.complement = complement;
        this.neighborhood = validateNeighborhood(neighborhood);
        this.city = validateCity(city);
        this.state = validateState(state);
        this.zipCode = validateZipCode(zipCode);
        this.country = Objects.requireNonNullElse(country, "Brasil");
        this.type = Objects.requireNonNullElse(type, AddressTypeEnum.RESIDENTIAL);
        this.primary = primary;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Address create(UUID userId, String street, String number, String neighborhood,
                               String city, String state, String zipCode, AddressTypeEnum type) {
        return new Address(null, userId, street, number, null, neighborhood, city, state, zipCode,
                "Brasil", type, false, true, OffsetDateTime.now(), OffsetDateTime.now());
    }

    public static Address of(UUID id, UUID userId, String street, String number, String complement,
                           String neighborhood, String city, String state, String zipCode, String country,
                           AddressTypeEnum type, boolean primary, boolean active,
                           OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new Address(id, userId, street, number, complement, neighborhood, city, state, zipCode,
                country, type, primary, active, createdAt, updatedAt);
    }

    private String validateStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            throw new ValidationException("Rua é obrigatória");
        }
        String trimmed = street.trim();
        if (trimmed.length() < 2) {
            throw new ValidationException("Rua deve ter pelo menos 2 caracteres");
        }
        if (trimmed.length() > 200) {
            throw new ValidationException("Rua deve ter no máximo 200 caracteres");
        }
        return trimmed;
    }

    private String validateNumber(String number) {
        if (number == null || number.trim().isEmpty()) {
            throw new ValidationException("Número é obrigatório");
        }
        String trimmed = number.trim();
        if (trimmed.length() > 20) {
            throw new ValidationException("Número deve ter no máximo 20 caracteres");
        }
        return trimmed;
    }

    private String validateNeighborhood(String neighborhood) {
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new ValidationException("Bairro é obrigatório");
        }
        String trimmed = neighborhood.trim();
        if (trimmed.length() < 2) {
            throw new ValidationException("Bairro deve ter pelo menos 2 caracteres");
        }
        if (trimmed.length() > 100) {
            throw new ValidationException("Bairro deve ter no máximo 100 caracteres");
        }
        return trimmed;
    }

    private String validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new ValidationException("Cidade é obrigatória");
        }
        String trimmed = city.trim();
        if (trimmed.length() < 2) {
            throw new ValidationException("Cidade deve ter pelo menos 2 caracteres");
        }
        if (trimmed.length() > 100) {
            throw new ValidationException("Cidade deve ter no máximo 100 caracteres");
        }
        return trimmed;
    }

    private String validateState(String state) {
        if (state == null || state.trim().isEmpty()) {
            throw new ValidationException("Estado é obrigatório");
        }
        String trimmed = state.trim().toUpperCase();
        if (trimmed.length() != 2) {
            throw new ValidationException("Estado deve ter exatamente 2 caracteres (UF)");
        }
        return trimmed;
    }

    private String validateZipCode(String zipCode) {
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new ValidationException("CEP é obrigatório");
        }
        String cleaned = zipCode.replaceAll("[^0-9]", "");
        if (cleaned.length() != 8) {
            throw new ValidationException("CEP deve ter 8 dígitos");
        }
        return cleaned;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getStreet() { return street; }
    public String getNumber() { return number; }
    public String getComplement() { return complement; }
    public String getNeighborhood() { return neighborhood; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }
    public AddressTypeEnum getType() { return type; }
    public boolean isPrimary() { return primary; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    // Setters para campos mutáveis
    public void setStreet(String street) {
        this.street = validateStreet(street);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setNumber(String number) {
        this.number = validateNumber(number);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setComplement(String complement) {
        this.complement = complement;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = validateNeighborhood(neighborhood);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCity(String city) {
        this.city = validateCity(city);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setState(String state) {
        this.state = validateState(state);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setZipCode(String zipCode) {
        this.zipCode = validateZipCode(zipCode);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCountry(String country) {
        this.country = Objects.requireNonNull(country, "País é obrigatório");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setType(AddressTypeEnum type) {
        this.type = Objects.requireNonNull(type, "Tipo de endereço é obrigatório");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business methods
    public String getFormattedZipCode() {
        if (zipCode == null || zipCode.length() != 8) return zipCode;
        return zipCode.substring(0, 5) + "-" + zipCode.substring(5, 8);
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(", ").append(number);
        if (complement != null && !complement.trim().isEmpty()) {
            sb.append(", ").append(complement);
        }
        sb.append(" - ").append(neighborhood);
        sb.append(" - ").append(city).append("/").append(state);
        sb.append(" - ").append(getFormattedZipCode());
        return sb.toString();
    }

    public void markAsPrimary() {
        this.primary = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markAsSecondary() {
        this.primary = false;
        this.updatedAt = OffsetDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", userId=" + userId +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", type=" + type +
                ", primary=" + primary +
                ", active=" + active +
                '}';
    }
}
