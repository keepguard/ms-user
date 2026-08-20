package com.keepguard.ms_user.application.mapper;

import com.keepguard.ms_user.application.dto.user.UserCreateCommandDTO;
import com.keepguard.ms_user.application.dto.user.UserUpdateCommandDTO;
import com.keepguard.ms_user.application.dto.user.UserViewDTO;
import com.keepguard.ms_user.application.dto.user.UserSimpleViewDTO;
import com.keepguard.ms_user.application.dto.user.UserDetailsViewDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchViewDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchQueryDTO;
import com.keepguard.ms_user.application.dto.user.UserSearchCriteriaDTO;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.entity.UserProfile;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserApplicationMapper {

    public User toDomain(UserCreateCommandDTO command) {
        return User.create(
            UUID.randomUUID(), // codeUser será gerado na regra de negócio
            command.companyId(),
            command.tenantId(),
            command.type(),
            command.email(),
            command.phoneE164(),
            command.preferredLocale(),
            command.timezone(),
            command.avatarUrl()
        );
    }

    public UserViewDTO toView(User user) {
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
            null, // personProfile será carregado separadamente se necessário
            null, // companyProfile será carregado separadamente se necessário
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
            null, // personProfile será carregado separadamente se necessário
            null, // companyProfile será carregado separadamente se necessário
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public UserDetailsViewDTO toGetByIdView(User user, UserProfile profile) {
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
                profile instanceof PersonProfile person ? person : null,
                profile instanceof CompanyProfile company ? company : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserDetailsViewDTO toByCodeUserView(User user, UserProfile profile) {
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
                profile instanceof PersonProfile person ? person : null,
                profile instanceof CompanyProfile company ? company : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserDetailsViewDTO toByEmailView(User user, UserProfile profile) {
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
                profile instanceof PersonProfile person ? person : null,
                profile instanceof CompanyProfile company ? company : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
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
            profile instanceof PersonProfile person ? person : null,
            profile instanceof CompanyProfile company ? company : null,
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
