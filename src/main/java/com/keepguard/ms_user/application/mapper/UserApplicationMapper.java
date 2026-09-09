package com.keepguard.ms_user.application.mapper;

import com.keepguard.ms_user.application.dto.profile.CompanyProfileCommandDTO;
import com.keepguard.ms_user.application.dto.profile.CompanyProfileViewDTO;
import com.keepguard.ms_user.application.dto.profile.PersonProfileCommandDTO;
import com.keepguard.ms_user.application.dto.profile.PersonProfileViewDTO;
import com.keepguard.ms_user.application.dto.user.UserCreateCommandDTO;
import com.keepguard.ms_user.application.dto.user.UserDetailsViewDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchQueryDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchViewDTO;
import com.keepguard.ms_user.application.dto.user.UserSimpleViewDTO;
import com.keepguard.ms_user.application.dto.user.UserUpdateCommandDTO;
import com.keepguard.ms_user.application.dto.user.UserViewDTO;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.entity.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserApplicationMapper {

    public User toDomain(UserCreateCommandDTO command) {
        UUID tenantId = command.tenantId() != null ? command.tenantId() : command.companyId();
        return User.create(
            UUID.randomUUID(),
            command.companyId(),
            tenantId,
            command.type(),
            command.email(),
            command.phoneE164(),
            command.preferredLocale(),
            command.timezone(),
            command.avatarUrl()
        );
    }

    public PersonProfile toPersonProfile(PersonProfileCommandDTO command) {
        if (command == null) {
            return null;
        }
        return PersonProfile.of(
            null,
            null,
            command.fullName(),
            command.cpf(),
            command.rg(),
            command.rgIssuer(),
            command.rgState(),
            command.dateOfBirth(),
            command.gender(),
            command.maritalStatus(),
            command.nationality(),
            command.birthCountry(),
            command.birthState(),
            command.birthCity(),
            command.motherName(),
            command.fatherName(),
            command.pep(),
            command.kycStatus(),
            command.kycLevel(),
            command.occupation(),
            command.incomeRange(),
            null,
            null
        );
    }

    public CompanyProfile toCompanyProfile(CompanyProfileCommandDTO command) {
        if (command == null) {
            return null;
        }
        return CompanyProfile.of(
            null,
            null,
            command.companyId(),
            command.legalNameSnapshot(),
            command.cnpjSnapshot(),
            command.stateRegistrationSnapshot(),
            command.municipalRegistrationSnapshot(),
            command.representativeName(),
            command.representativeCpf(),
            null,
            null
        );
    }

    public PersonProfileViewDTO toPersonProfileView(PersonProfile profile) {
        if (profile == null) {
            return null;
        }
        return new PersonProfileViewDTO(
            profile.getUserId(),
            profile.getFullName(),
            profile.getCpf(),
            profile.getRg(),
            profile.getRgIssuer(),
            profile.getRgState(),
            profile.getDateOfBirth(),
            profile.getGender(),
            profile.getMaritalStatus(),
            profile.getNationality(),
            profile.getBirthCountry(),
            profile.getBirthState(),
            profile.getBirthCity(),
            profile.getMotherName(),
            profile.getFatherName(),
            profile.isPep(),
            profile.getKycStatus(),
            profile.getKycLevel(),
            profile.getOccupation(),
            profile.getIncomeRange(),
            profile.getCreatedAt(),
            profile.getUpdatedAt()
        );
    }

    public CompanyProfileViewDTO toCompanyProfileView(CompanyProfile profile) {
        if (profile == null) {
            return null;
        }
        return new CompanyProfileViewDTO(
            profile.getUserId(),
            profile.getCompanyId(),
            profile.getLegalNameSnapshot(),
            profile.getCnpjSnapshot(),
            profile.getStateRegistrationSnapshot(),
            profile.getMunicipalRegistrationSnapshot(),
            profile.getRepresentativeName(),
            profile.getRepresentativeCpf(),
            profile.getCreatedAt(),
            profile.getUpdatedAt()
        );
    }

    public UserViewDTO toView(User user) {
        return toView(user, null);
    }

    public UserViewDTO toView(User user, UserProfile profile) {
        return new UserViewDTO(
            user.getId(),
            user.getCodeUser(),
            user.getCompanyId(),
            user.getType(),
            user.getStatus(),
            user.getEmail(),
            user.getPhoneE164(),
            user.getPreferredLocale(),
            user.getTimezone(),
            user.getAvatarUrl(),
            user.getDisplayHandle(),
            profile instanceof PersonProfile person ? toPersonProfileView(person) : null,
            profile instanceof CompanyProfile company ? toCompanyProfileView(company) : null,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public UserSimpleViewDTO toSimpleView(User user) {
        return new UserSimpleViewDTO(
            user.getId(),
            user.getCodeUser(),
            user.getEmail(),
            user.getStatus(),
            user.getType(),
            user.getCreatedAt()
        );
    }

    public UserDetailsViewDTO toDetailsView(User user) {
        return toDetailsView(user, null);
    }

    public UserDetailsViewDTO toDetailsView(User user, UserProfile profile) {
        return new UserDetailsViewDTO(
            user.getId(),
            user.getCodeUser(),
            user.getCompanyId(),
            user.getType(),
            user.getStatus(),
            user.getEmail(),
            user.getPhoneE164(),
            user.getPreferredLocale(),
            user.getTimezone(),
            user.getAvatarUrl(),
            user.getDisplayHandle(),
            profile instanceof PersonProfile person ? toPersonProfileView(person) : null,
            profile instanceof CompanyProfile company ? toCompanyProfileView(company) : null,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public UserDetailsViewDTO toGetByIdView(User user, UserProfile profile) {
        return toDetailsView(user, profile);
    }

    public UserDetailsViewDTO toByCodeUserView(User user, UserProfile profile) {
        return toDetailsView(user, profile);
    }

    public UserDetailsViewDTO toByEmailView(User user, UserProfile profile) {
        return toDetailsView(user, profile);
    }

    public UserSearchViewDTO toSearchView(User user, UserProfile profile) {
        return new UserSearchViewDTO(
            user.getId(),
            user.getCodeUser(),
            user.getCompanyId(),
            user.getEmail(),
            user.getStatus(),
            user.getType(),
            user.getAvatarUrl(),
            user.getDisplayHandle(),
            profile instanceof PersonProfile person ? toPersonProfileView(person) : null,
            profile instanceof CompanyProfile company ? toCompanyProfileView(company) : null,
            user.getCreatedAt()
        );
    }

    public User applyChanges(User user, UserUpdateCommandDTO command) {
        if (command.email().isPresent()) {
            user.setEmail(command.email().get());
        }
        if (command.phoneE164().isPresent()) {
            user.setPhoneE164(command.phoneE164().get());
        }
        if (command.preferredLocale().isPresent()) {
            user.setPreferredLocale(command.preferredLocale().get());
        }
        if (command.timezone().isPresent()) {
            user.setTimezone(command.timezone().get());
        }
        if (command.avatarUrl().isPresent()) {
            user.setAvatarUrl(command.avatarUrl().get());
        }
        if (command.status().isPresent()) {
            user.setStatus(command.status().get());
        }
        return user;
    }

    public UserSearchCriteriaDTO toSearchCriteria(UserSearchQueryDTO query) {
        if (query == null) {
            return null;
        }
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
}
