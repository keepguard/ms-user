package com.keepguard.ms_user.adapters.in.rest.user.mapper;

import com.keepguard.ms_user.adapters.in.rest.user.dto.request.*;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.*;
import com.keepguard.ms_user.application.dto.user.*;
import com.keepguard.ms_user.application.dto.user.UserViewDTO;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class UserAdapterMapper {

    public UserCreateCommandDTO toCreateCommand(UserCreateRequestDTO request, UUID xApplication) {
        String displayHandle = request.personProfile() != null ? request.personProfile().displayHandle() : null;
        return new UserCreateCommandDTO(
            request.companyId(),
            xApplication,
            request.type(),
            request.email(),
            request.phoneE164(),
            request.preferredLocale(),
            request.timezone(),
            request.avatarUrl(),
            displayHandle,
            request.personProfile() != null ? toPersonProfile(request.personProfile()) : null,
            request.companyProfile() != null ? toCompanyProfile(request.companyProfile()) : null
        );
    }

    public UserUpdateCommandDTO toUpdateCommand(UserUpdateRequestDTO request, UUID id, UUID xApplication) {
        java.util.Optional<String> displayHandle = request.personProfile() != null && request.personProfile().displayHandle() != null
            ? java.util.Optional.of(request.personProfile().displayHandle())
            : java.util.Optional.empty();
        return new UserUpdateCommandDTO(
            id,
            xApplication,
            java.util.Optional.ofNullable(request.companyId()),
            java.util.Optional.ofNullable(request.codeUser()),
            java.util.Optional.ofNullable(request.type()),
            java.util.Optional.ofNullable(request.status()),
            java.util.Optional.ofNullable(request.email()),
            java.util.Optional.ofNullable(request.phoneE164()),
            java.util.Optional.ofNullable(request.preferredLocale()),
            java.util.Optional.ofNullable(request.timezone()),
            java.util.Optional.ofNullable(request.avatarUrl()),
            displayHandle,
            request.personProfile() != null ? java.util.Optional.of(toPersonProfile(request.personProfile())) : java.util.Optional.empty(),
            request.companyProfile() != null ? java.util.Optional.of(toCompanyProfile(request.companyProfile())) : java.util.Optional.empty()
        );
    }

    // === Query Methods ===

    public UserGetByIdQueryDTO toGetByIdQuery(UUID id, UUID xApplication) {
        return new UserGetByIdQueryDTO(id, xApplication);
    }

    public UserGetByCodeUserQueryDTO toGetByCodeUserQuery(UUID codeUser, UUID xApplication) {
        return new UserGetByCodeUserQueryDTO(codeUser, xApplication);
    }

    public UserGetByEmailQueryDTO toGetByEmailQuery(String email, UUID xApplication) {
        return new UserGetByEmailQueryDTO(email, xApplication);
    }

    public UserSearchQueryDTO toSearchQuery(UserSearchRequestDTO request, UUID xApplication, UUID companyId) {
        // Parse enum types usando métodos estáticos dos enums
        var userType = UserTypeEnum.fromString(request.getType());
        var userStatus = UserStatusEnum.fromString(request.getStatus());
        
        return new UserSearchQueryDTO(
            xApplication,
            request.getEmail(),
            companyId,
            userType,
            userStatus,
            request.getPage() != null ? request.getPage() : 0,
            request.getSize() != null ? request.getSize() : 20,
            request.getSort(),
            request.getDirection() != null ? request.getDirection() : "ASC"
        );
    }

    public UserSearchCriteriaDTO toSearchCriteria(UserSearchQueryDTO query) {
        return new UserSearchCriteriaDTO(
            query.email(),
            query.companyId(),
            query.type(),
            query.status(),
            query.page(),
            query.size(),
            query.sortFields(),
            query.sortDirection()
        );
    }

    // === Command Methods ===

    public UserDeleteCommandDTO toDeleteCommand(UUID id, UUID xApplication) {
        return new UserDeleteCommandDTO(id, xApplication);
    }

    public UserStatusChangeCommandDTO toStatusChangeCommand(UUID id, String reason, UUID xApplication) {
        return new UserStatusChangeCommandDTO(id, xApplication, reason);
    }

    public UserBatchStatusCommandDTO toBatchStatusCommand(List<UUID> userIds, String reason, UUID xApplication) {
        return new UserBatchStatusCommandDTO(userIds, xApplication, reason);
    }

    public UserResponseDTO toGetByIdResponseDTO(UserDetailsViewDTO view) {
        if (view == null) {
            return null;
        }

        var dto = new UserResponseDTO();
        dto.setId(view.id());
        dto.setCodeUser(view.codeUser());
        dto.setCompanyId(view.companyId());
        dto.setType(view.type());
        dto.setEmail(view.email());
        dto.setPhoneE164(view.phoneE164());
        dto.setPreferredLocale(view.preferredLocale());
        dto.setTimezone(view.timezone());
        dto.setAvatarUrl(view.avatarUrl());
        dto.setDisplayHandle(view.displayHandle());
        dto.setStatus(view.status());
        dto.setPersonProfile(view.personProfile() != null ? toPersonResponseDTO(view.personProfile()) : null);
        dto.setCompanyProfile(view.companyProfile() != null ? toCompanyResponseDTO(view.companyProfile()) : null);
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(view.updatedAt());
        return dto;
    }

    public UserResponseDTO toGetByCodeUserResponseDTO(UserDetailsViewDTO view) {
        if (view == null) {
            return null;
        }

        var dto = new UserResponseDTO();
        dto.setId(view.id());
        dto.setCodeUser(view.codeUser());
        dto.setCompanyId(view.companyId());
        dto.setType(view.type());
        dto.setEmail(view.email());
        dto.setPhoneE164(view.phoneE164());
        dto.setPreferredLocale(view.preferredLocale());
        dto.setTimezone(view.timezone());
        dto.setAvatarUrl(view.avatarUrl());
        dto.setDisplayHandle(view.displayHandle());
        dto.setStatus(view.status());
        dto.setPersonProfile(view.personProfile() != null ? toPersonResponseDTO(view.personProfile()) : null);
        dto.setCompanyProfile(view.companyProfile() != null ? toCompanyResponseDTO(view.companyProfile()) : null);
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(view.updatedAt());
        return dto;
    }

    public UserResponseDTO toGetByEmail(UserDetailsViewDTO view) {
        if (view == null) {
            return null;
        }

        var dto = new UserResponseDTO();
        dto.setId(view.id());
        dto.setCodeUser(view.codeUser());
        dto.setCompanyId(view.companyId());
        dto.setType(view.type());
        dto.setEmail(view.email());
        dto.setPhoneE164(view.phoneE164());
        dto.setPreferredLocale(view.preferredLocale());
        dto.setTimezone(view.timezone());
        dto.setAvatarUrl(view.avatarUrl());
        dto.setDisplayHandle(view.displayHandle());
        dto.setStatus(view.status());
        dto.setPersonProfile(view.personProfile() != null ? toPersonResponseDTO(view.personProfile()) : null);
        dto.setCompanyProfile(view.companyProfile() != null ? toCompanyResponseDTO(view.companyProfile()) : null);
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(view.updatedAt());
        return dto;
    }

    public UserResponseDTO toResponseDTO(UserDetailsViewDTO view) {
        if (view == null) {
            return null;
        }

        var dto = new UserResponseDTO();
        dto.setId(view.id());
        dto.setCodeUser(view.codeUser());
        dto.setCompanyId(view.companyId());
        dto.setType(view.type());
        dto.setEmail(view.email());
        dto.setPhoneE164(view.phoneE164());
        dto.setPreferredLocale(view.preferredLocale());
        dto.setTimezone(view.timezone());
        dto.setAvatarUrl(view.avatarUrl());
        dto.setDisplayHandle(view.displayHandle());
        dto.setStatus(view.status());
        dto.setPersonProfile(null); // TODO: Implementar conversão de PersonProfile para PersonResponseDTO
        dto.setCompanyProfile(null); // TODO: Implementar conversão de CompanyProfile para CompanyResponseDTO
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(view.updatedAt());
        return dto;
    }

    public UserResponseDTO toResponseDTO(UserViewDTO view) {
        if (view == null) {
            return null;
        }

        var dto = new UserResponseDTO();
        dto.setId(view.id());
        dto.setCodeUser(view.codeUser());
        dto.setCompanyId(view.companyId());
        dto.setType(view.type());
        dto.setEmail(view.email());
        dto.setPhoneE164(view.phoneE164());
        dto.setPreferredLocale(view.preferredLocale());
        dto.setTimezone(view.timezone());
        dto.setAvatarUrl(view.avatarUrl());
        dto.setDisplayHandle(view.displayHandle());
        dto.setStatus(view.status());
        dto.setPersonProfile(null); // Será carregado separadamente se necessário
        dto.setCompanyProfile(null); // Será carregado separadamente se necessário
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(view.updatedAt());
        return dto;
    }

    public UserResponseDTO toResponseDTO(UserSearchViewDTO view) {
        if (view == null) {
            return null;
        }

        var dto = new UserResponseDTO();
        dto.setId(view.id());
        dto.setCodeUser(view.codeUser());
        dto.setCompanyId(view.companyId());
        dto.setType(view.type());
        dto.setEmail(view.email());
        dto.setPhoneE164(null); // UserSearchViewDTO não tem phoneE164
        dto.setPreferredLocale(null); // UserSearchViewDTO não tem preferredLocale
        dto.setTimezone(null); // UserSearchViewDTO não tem timezone
        dto.setAvatarUrl(view.avatarUrl());
        dto.setDisplayHandle(view.displayHandle());
        dto.setStatus(view.status());
        // Converter profiles se existirem
        if (view.personProfile() != null) {
            dto.setPersonProfile(toPersonResponseDTO(view.personProfile()));
        } else {
            dto.setPersonProfile(null);
        }
        
        if (view.companyProfile() != null) {
            dto.setCompanyProfile(toCompanyResponseDTO(view.companyProfile()));
        } else {
            dto.setCompanyProfile(null);
        }
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(null); // UserSearchViewDTO não tem updatedAt
        return dto;
    }


    public UserStatusResponseDTO toStatusResponse(UserDetailsViewDTO view, UserStatusEnum previousStatus, String reason) {
        var dto = new UserStatusResponseDTO();
        dto.setUserId(view.id());
        dto.setPreviousStatus(previousStatus);
        dto.setNewStatus(view.status());
        dto.setReason(reason);
        dto.setChangedAt(OffsetDateTime.now());
        dto.setCanPerformOperations(canPerformOperations(view.status()));
        return dto;
    }

    public UserStatusResponseDTO toStatusResponse(UserViewDTO view, UserStatusEnum previousStatus, String reason) {
        var dto = new UserStatusResponseDTO();
        dto.setUserId(view.id());
        dto.setPreviousStatus(previousStatus);
        dto.setNewStatus(view.status());
        dto.setReason(reason);
        dto.setChangedAt(OffsetDateTime.now());
        dto.setCanPerformOperations(canPerformOperations(view.status()));
        return dto;
    }


    private boolean canPerformOperations(UserStatusEnum status) {
        return UserStatusEnum.ACTIVE.equals(status) || UserStatusEnum.PENDING.equals(status);
    }



    private PersonProfile toPersonProfile(PersonRequestDTO request) {
        return com.keepguard.ms_user.domain.entity.PersonProfile.of(
            null, // userId será definido pelas strategies
            request.fullName(),
            request.cpf(),
            request.rg(),
            request.rgIssuer(),
            request.rgState(),
            request.dateOfBirth(),
            request.gender(),
            request.maritalStatus(),
            request.nationality(),
            request.birthCountry(),
            request.birthState(),
            request.birthCity(),
            request.motherName(),
            request.fatherName(),
            request.pep(),
            request.kycStatus(),
            request.kycLevel(),
            request.occupation(),
            request.incomeRange(),
            null, // createdAt será definido pelas strategies
            null  // updatedAt será definido pelas strategies
        );
    }

    private CompanyProfile toCompanyProfile(CompanyRequestDTO request) {
        return com.keepguard.ms_user.domain.entity.CompanyProfile.of(
            null, // userId será definido pelas strategies
            request.companyId(),
            request.legalNameSnapshot(),
            request.cnpjSnapshot(),
            request.stateRegistrationSnapshot(),
            request.municipalRegistrationSnapshot(),
            request.representativeName(),
            request.representativeCpf(),
            null, // createdAt será definido pelas strategies
            null  // updatedAt será definido pelas strategies
        );
    }

    private PersonResponseDTO toPersonResponseDTO(PersonProfile personProfile) {
        if (personProfile == null) {
            return null;
        }
        
        return new PersonResponseDTO(
            personProfile.getUserId(),
            personProfile.getFullName(),
            personProfile.getCpf(),
            personProfile.getRg(),
            personProfile.getRgIssuer(),
            personProfile.getRgState(),
            personProfile.getDateOfBirth(),
            personProfile.getGender(),
            personProfile.getMaritalStatus(),
            personProfile.getNationality(),
            personProfile.getBirthCountry(),
            personProfile.getBirthState(),
            personProfile.getBirthCity(),
            personProfile.getMotherName(),
            personProfile.getFatherName(),
            personProfile.isPep(),
            personProfile.getKycStatus(),
            personProfile.getKycLevel(),
            personProfile.getOccupation(),
            personProfile.getIncomeRange(),
            personProfile.getCreatedAt(),
            personProfile.getUpdatedAt()
        );
    }

    /**
     * Converte CompanyProfile (domínio) para CompanyResponseDTO
     */
    private CompanyResponseDTO toCompanyResponseDTO(com.keepguard.ms_user.domain.entity.CompanyProfile companyProfile) {
        if (companyProfile == null) {
            return null;
        }
        
        return new CompanyResponseDTO(
            companyProfile.getUserId(),
            companyProfile.getCompanyId(),
            companyProfile.getLegalNameSnapshot(),
            companyProfile.getCnpjSnapshot(),
            companyProfile.getStateRegistrationSnapshot(),
            companyProfile.getMunicipalRegistrationSnapshot(),
            companyProfile.getRepresentativeName(),
            companyProfile.getRepresentativeCpf(),
            companyProfile.getCreatedAt(),
            companyProfile.getUpdatedAt()
        );
    }
}
