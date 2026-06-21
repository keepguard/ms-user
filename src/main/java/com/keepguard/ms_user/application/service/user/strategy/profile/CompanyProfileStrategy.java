package com.keepguard.ms_user.application.service.user.strategy.profile;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.application.port.out.persistence.CompanyProfileRepositoryPort;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyProfileStrategy implements ProfileStrategy {

    private final CompanyProfileRepositoryPort companyProfileRepositoryPort;

    @Override
    public boolean supports(UserTypeEnum userType) {
        return UserTypeEnum.COMPANY.equals(userType);
    }

    @Override
    public void createProfile(User user, Object profileData) {
        log.info("CompanyProfileStrategy.createProfile chamado para usuário: {}", user.getId());

        if (!(profileData instanceof CompanyProfile companyProfile)) {
            log.warn("Tipo de dados inválido para CompanyProfile: {}", profileData != null ? profileData.getClass() : "null");
            return;
        }

        // Criar novo CompanyProfile com o userId correto
        var newCompanyProfile = CompanyProfile.of(
            user.getId(),
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

        companyProfileRepositoryPort.save(newCompanyProfile);
        log.info("CompanyProfile criado com sucesso para usuário: {}", user.getId());
    }

    @Override
    public void updateProfile(UUID userId, Object profileData) {
        if (!(profileData instanceof CompanyProfile companyProfile)) {
            log.warn("Tipo de dados inválido para CompanyProfile: {}", profileData.getClass());
            return;
        }

        log.info("Atualizando CompanyProfile para usuário: {}", userId);
        // Buscar o perfil existente e atualizar os campos
        var existingProfile = companyProfileRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new ValidationException("CompanyProfile não encontrado para usuário: " + userId));

        // Atualizar campos mutáveis
        existingProfile.setCompanyId(companyProfile.getCompanyId());
        existingProfile.setLegalNameSnapshot(companyProfile.getLegalNameSnapshot());
        existingProfile.setCnpjSnapshot(companyProfile.getCnpjSnapshot());
        existingProfile.setStateRegistrationSnapshot(companyProfile.getStateRegistrationSnapshot());
        existingProfile.setMunicipalRegistrationSnapshot(companyProfile.getMunicipalRegistrationSnapshot());
        existingProfile.setRepresentativeName(companyProfile.getRepresentativeName());
        existingProfile.setRepresentativeCpf(companyProfile.getRepresentativeCpf());

        companyProfileRepositoryPort.save(existingProfile);
        log.info("CompanyProfile atualizado com sucesso para usuário: {}", userId);
    }

    @Override
    public void deleteProfile(UUID userId) {
        log.info("Removendo CompanyProfile para usuário: {}", userId);
        companyProfileRepositoryPort.deleteByUserId(userId);
        log.info("CompanyProfile removido com sucesso para usuário: {}", userId);
    }

    @Override
    public Object getProfile(UUID userId) {
        log.debug("Buscando CompanyProfile para usuário: {}", userId);
        return companyProfileRepositoryPort.findByUserId(userId).orElse(null);
    }
}
