package com.keepguard.ms_user.adapters.in.rest.user.mapper;

import com.keepguard.ms_user.adapters.in.rest.user.dto.request.*;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.CompanyResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.PersonResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.UserResponseDTO;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.UserStatusResponseDTO;
import com.keepguard.ms_user.application.dto.profile.CompanyProfileCommandDTO;
import com.keepguard.ms_user.application.dto.profile.CompanyProfileViewDTO;
import com.keepguard.ms_user.application.dto.profile.PersonProfileCommandDTO;
import com.keepguard.ms_user.application.dto.profile.PersonProfileViewDTO;
import com.keepguard.ms_user.application.dto.user.*;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class UserAdapterMapper {

    public UserCreateCommandDTO toCreateCommand(UserCreateRequestDTO request, UUID companyId) {
        return toCreateCommand(request, companyId, null);
    }

    public UserCreateCommandDTO toCreateCommand(UserCreateRequestDTO request, UUID companyId, UUID tenantId) {
        String displayHandle = request.personProfile() != null ? request.personProfile().displayHandle() : null;
        UUID resolvedTenantId = tenantId != null ? tenantId : companyId;
        return new UserCreateCommandDTO(
            companyId,
            resolvedTenantId,
            request.type(),
            request.email(),
            request.phoneE164(),
            request.preferredLocale(),
            request.timezone(),
            request.avatarUrl(),
            displayHandle,
            request.personProfile() != null ? toPersonProfileCommand(request.personProfile()) : null,
            request.companyProfile() != null ? toCompanyProfileCommand(request.companyProfile()) : null
        );
    }

    public UserUpdateCommandDTO toUpdateCommand(UserUpdateRequestDTO request, UUID id, UUID companyId) {
        Optional<String> displayHandle = request.personProfile() != null && request.personProfile().displayHandle() != null
            ? Optional.of(request.personProfile().displayHandle())
            : Optional.empty();
        return new UserUpdateCommandDTO(
            id,
            companyId,
            Optional.ofNullable(request.codeUser()),
            Optional.ofNullable(request.type()),
            Optional.ofNullable(request.status()),
            Optional.ofNullable(request.email()),
            Optional.ofNullable(request.phoneE164()),
            Optional.ofNullable(request.preferredLocale()),
            Optional.ofNullable(request.timezone()),
            Optional.ofNullable(request.avatarUrl()),
            displayHandle,
            request.personProfile() != null ? Optional.of(toPersonProfileCommand(request.personProfile())) : Optional.empty(),
            request.companyProfile() != null ? Optional.of(toCompanyProfileCommand(request.companyProfile())) : Optional.empty()
        );
    }

    public UserGetByIdQueryDTO toGetByIdQuery(UUID id, UUID companyId) {
        return new UserGetByIdQueryDTO(id, companyId);
    }

    public UserGetByCodeUserQueryDTO toGetByCodeUserQuery(UUID codeUser, UUID companyId) {
        return new UserGetByCodeUserQueryDTO(codeUser, companyId);
    }

    public UserGetByEmailQueryDTO toGetByEmailQuery(String email, UUID companyId) {
        return new UserGetByEmailQueryDTO(email, companyId);
    }

    public UserSearchQueryDTO toSearchQuery(UserSearchRequestDTO request, UUID companyId) {
        var userType = UserTypeEnum.fromString(request.getType());
        var userStatus = UserStatusEnum.fromString(request.getStatus());

        return new UserSearchQueryDTO(
            companyId,
            request.getEmail(),
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

    public UserDeleteCommandDTO toDeleteCommand(UUID id, UUID companyId) {
        return new UserDeleteCommandDTO(id, companyId);
    }

    public UserPatchPersonDocumentCommandDTO toPatchPersonDocumentCommand(
            PersonDocumentPatchRequestDTO request, UUID id, UUID companyId) {
        return new UserPatchPersonDocumentCommandDTO(id, companyId, request.cpf());
    }

    public UserStatusChangeCommandDTO toStatusChangeCommand(UUID id, String reason, UUID companyId) {
        return new UserStatusChangeCommandDTO(id, companyId, reason);
    }

    public UserBatchStatusCommandDTO toBatchStatusCommand(List<UUID> userIds, String reason, UUID companyId) {
        return new UserBatchStatusCommandDTO(userIds, companyId, reason);
    }

    public UserResponseDTO toGetByIdResponseDTO(UserDetailsViewDTO view) {
        return toResponseDTO(view);
    }

    public UserResponseDTO toGetByCodeUserResponseDTO(UserDetailsViewDTO view) {
        return toResponseDTO(view);
    }

    public UserResponseDTO toGetByEmail(UserDetailsViewDTO view) {
        return toResponseDTO(view);
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
        dto.setPersonProfile(toPersonResponseDTO(view.personProfile()));
        dto.setCompanyProfile(toCompanyResponseDTO(view.companyProfile()));
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
        dto.setPersonProfile(toPersonResponseDTO(view.personProfile()));
        dto.setCompanyProfile(toCompanyResponseDTO(view.companyProfile()));
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
        dto.setPhoneE164(null);
        dto.setPreferredLocale(null);
        dto.setTimezone(null);
        dto.setAvatarUrl(view.avatarUrl());
        dto.setDisplayHandle(view.displayHandle());
        dto.setStatus(view.status());
        dto.setPersonProfile(toPersonResponseDTO(view.personProfile()));
        dto.setCompanyProfile(toCompanyResponseDTO(view.companyProfile()));
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(null);
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

    private PersonProfileCommandDTO toPersonProfileCommand(PersonRequestDTO request) {
        return new PersonProfileCommandDTO(
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
            request.incomeRange()
        );
    }

    private CompanyProfileCommandDTO toCompanyProfileCommand(CompanyRequestDTO request) {
        return new CompanyProfileCommandDTO(
            request.companyId(),
            request.legalNameSnapshot(),
            request.cnpjSnapshot(),
            request.stateRegistrationSnapshot(),
            request.municipalRegistrationSnapshot(),
            request.representativeName(),
            request.representativeCpf()
        );
    }

    private PersonResponseDTO toPersonResponseDTO(PersonProfileViewDTO personProfile) {
        if (personProfile == null) {
            return null;
        }
        return new PersonResponseDTO(
            personProfile.userId(),
            personProfile.fullName(),
            personProfile.cpf(),
            personProfile.rg(),
            personProfile.rgIssuer(),
            personProfile.rgState(),
            personProfile.dateOfBirth(),
            personProfile.gender(),
            personProfile.maritalStatus(),
            personProfile.nationality(),
            personProfile.birthCountry(),
            personProfile.birthState(),
            personProfile.birthCity(),
            personProfile.motherName(),
            personProfile.fatherName(),
            personProfile.pep(),
            personProfile.kycStatus(),
            personProfile.kycLevel(),
            personProfile.occupation(),
            personProfile.incomeRange(),
            personProfile.createdAt(),
            personProfile.updatedAt()
        );
    }

    private CompanyResponseDTO toCompanyResponseDTO(CompanyProfileViewDTO companyProfile) {
        if (companyProfile == null) {
            return null;
        }
        return new CompanyResponseDTO(
            companyProfile.userId(),
            companyProfile.companyId(),
            companyProfile.legalNameSnapshot(),
            companyProfile.cnpjSnapshot(),
            companyProfile.stateRegistrationSnapshot(),
            companyProfile.municipalRegistrationSnapshot(),
            companyProfile.representativeName(),
            companyProfile.representativeCpf(),
            companyProfile.createdAt(),
            companyProfile.updatedAt()
        );
    }
}
