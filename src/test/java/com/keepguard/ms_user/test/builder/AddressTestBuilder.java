package com.keepguard.ms_user.test.builder;

import com.keepguard.ms_user.adapters.in.rest.address.dto.request.AddressCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.address.dto.request.AddressSearchRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.address.dto.request.AddressUpdateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.address.dto.response.AddressDetailsResponseDTO;
import com.keepguard.ms_user.application.dto.address.*;
import com.keepguard.ms_user.domain.entity.Address;
import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.AddressJpaEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Builder para testes de Address
 */
public class AddressTestBuilder {
    
    private UUID id = UUID.randomUUID();
    private UUID userId = UUID.randomUUID();
    private UUID xApplication = UUID.randomUUID();
    private String street = "Rua das Flores";
    private String number = "123";
    private String complement = "Apto 45";
    private String neighborhood = "Centro";
    private String city = "São Paulo";
    private String state = "SP";
    private String zipCode = "01234567";
    private String country = "Brasil";
    private AddressTypeEnum type = AddressTypeEnum.RESIDENTIAL;
    private Boolean primary = true;
    private Boolean active = true;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    
    private AddressTestBuilder() {}
    
    public static AddressTestBuilder builder() {
        return new AddressTestBuilder();
    }
    
    public static AddressTestBuilder anAddress() {
        return new AddressTestBuilder();
    }
    
    public AddressTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }
    
    public AddressTestBuilder withUserId(UUID userId) {
        this.userId = userId;
        return this;
    }
    
    public AddressTestBuilder withXApplication(UUID xApplication) {
        this.xApplication = xApplication;
        return this;
    }
    
    public AddressTestBuilder withStreet(String street) {
        this.street = street;
        return this;
    }
    
    public AddressTestBuilder withNumber(String number) {
        this.number = number;
        return this;
    }
    
    public AddressTestBuilder withComplement(String complement) {
        this.complement = complement;
        return this;
    }
    
    public AddressTestBuilder withNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
        return this;
    }
    
    public AddressTestBuilder withCity(String city) {
        this.city = city;
        return this;
    }
    
    public AddressTestBuilder withState(String state) {
        this.state = state;
        return this;
    }
    
    public AddressTestBuilder withZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }
    
    public AddressTestBuilder withCountry(String country) {
        this.country = country;
        return this;
    }
    
    public AddressTestBuilder withType(AddressTypeEnum type) {
        this.type = type;
        return this;
    }
    
    public AddressTestBuilder withPrimary(Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public AddressTestBuilder withActive(Boolean active) {
        this.active = active;
        return this;
    }
    
    public AddressTestBuilder asResidential() {
        this.type = AddressTypeEnum.RESIDENTIAL;
        return this;
    }
    
    public AddressTestBuilder asCommercial() {
        this.type = AddressTypeEnum.COMMERCIAL;
        return this;
    }
    
    public AddressTestBuilder asPrimary() {
        this.primary = true;
        return this;
    }
    
    public AddressTestBuilder asSecondary() {
        this.primary = false;
        return this;
    }
    
    public AddressTestBuilder asActive() {
        this.active = true;
        return this;
    }
    
    public AddressTestBuilder asInactive() {
        this.active = false;
        return this;
    }
    
    public Address buildDomain() {
        return Address.of(
                id,
                userId,
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode,
                country,
                type,
                primary,
                active,
                createdAt,
                updatedAt
        );
    }
    
    public AddressCreateCommandDTO buildCreateCommand() {
        return new AddressCreateCommandDTO(
                userId,
                xApplication,
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode,
                country,
                type,
                primary,
                active
        );
    }
    
    public AddressUpdateCommandDTO buildUpdateCommand() {
        return new AddressUpdateCommandDTO(
                id,
                xApplication,
                Optional.of(street),
                Optional.of(number),
                Optional.of(complement),
                Optional.of(neighborhood),
                Optional.of(city),
                Optional.of(state),
                Optional.of(zipCode),
                Optional.of(country),
                Optional.of(type),
                Optional.of(primary),
                Optional.of(active)
        );
    }
    
    public AddressUpdateCommandDTO buildUpdateCommandWithPrimary(Boolean primary) {
        return new AddressUpdateCommandDTO(
                id,
                xApplication,
                Optional.of(street),
                Optional.of(number),
                Optional.of(complement),
                Optional.of(neighborhood),
                Optional.of(city),
                Optional.of(state),
                Optional.of(zipCode),
                Optional.of(country),
                Optional.of(type),
                Optional.of(primary),
                Optional.of(active)
        );
    }
    
    public AddressDeleteCommandDTO buildDeleteCommand() {
        return new AddressDeleteCommandDTO(id, xApplication);
    }
    
    public AddressGetByIdQueryDTO buildGetByIdQuery() {
        return new AddressGetByIdQueryDTO(id, xApplication);
    }
    
    public AddressGetByUserIdQueryDTO buildGetByUserIdQuery() {
        return new AddressGetByUserIdQueryDTO(userId, xApplication);
    }
    
    public AddressSearchQueryDTO buildSearchQuery() {
        return new AddressSearchQueryDTO(
                xApplication,
                userId,
                city,
                state,
                zipCode,
                type,
                primary,
                active,
                0,
                20,
                List.of("createdAt"),
                "DESC"
        );
    }
    
    public AddressDetailsViewDTO buildDetailsView() {
        return new AddressDetailsViewDTO(
                id,
                userId,
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode.substring(0, 5) + "-" + zipCode.substring(5),
                country,
                type,
                primary,
                active,
                street + ", " + number + " - " + neighborhood + " - " + city + "/" + state,
                createdAt,
                updatedAt
        );
    }
    
    public AddressSearchViewDTO buildSearchView() {
        return new AddressSearchViewDTO(
                id,
                userId,
                street,
                number,
                city,
                state,
                zipCode.substring(0, 5) + "-" + zipCode.substring(5),
                type,
                primary,
                active,
                createdAt
        );
    }
    
    public AddressJpaEntity buildJpaEntity() {
        return AddressJpaEntity.builder()
                .id(id)
                .userId(userId)
                .street(street)
                .number(number)
                .complement(complement)
                .neighborhood(neighborhood)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .country(country)
                .type(type)
                .primary(primary)
                .active(active)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
    
    // === Métodos para DTOs REST ===
    
    public AddressCreateRequestDTO buildCreateRequest() {
        return new AddressCreateRequestDTO(
                userId,
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode,
                country,
                type,
                primary,
                active
        );
    }
    
    public AddressUpdateRequestDTO buildUpdateRequest() {
        return new AddressUpdateRequestDTO(
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode,
                country,
                type,
                primary,
                active
        );
    }
    
    public AddressSearchRequestDTO buildSearchRequest() {
        AddressSearchRequestDTO dto = new AddressSearchRequestDTO();
        dto.setCity(city);
        dto.setState(state);
        dto.setType(type.name());
        dto.setPage(0);
        dto.setSize(20);
        return dto;
    }
    
    public AddressDetailsResponseDTO buildResponseDTO() {
        return new AddressDetailsResponseDTO(
                id,
                userId,
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode.substring(0, 5) + "-" + zipCode.substring(5),
                country,
                type,
                primary,
                active,
                street + ", " + number + " - " + neighborhood + " - " + city + "/" + state,
                createdAt,
                updatedAt
        );
    }
}

