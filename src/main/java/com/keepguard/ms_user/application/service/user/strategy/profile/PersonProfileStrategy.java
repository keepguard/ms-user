package com.keepguard.ms_user.application.service.user.strategy.profile;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.application.port.out.persistence.PersonProfileRepositoryPort;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonProfileStrategy implements ProfileStrategy {

    private final PersonProfileRepositoryPort personProfileRepositoryPort;

    @Override
    public boolean supports(UserTypeEnum userType) {
        return UserTypeEnum.PERSON.equals(userType);
    }

    @Override
    public void createProfile(User user, Object profileData) {
        if (!(profileData instanceof PersonProfile personProfile)) {
            log.warn("Tipo de dados inválido para PersonProfile: {}", profileData.getClass());
            return;
        }

        log.info("🔍 DEBUG: Criando PersonProfile para usuário: {} - CPF: {}", user.getId(), personProfile.getCpf());

        try {
            // display_handle: validado e persistido em User (UserCommandService)

            // Criar novo PersonProfile com o userId correto
            var now = OffsetDateTime.now();
            var newPersonProfile = PersonProfile.of(
                user.getId(),
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
                now,  // createdAt
                now   // updatedAt
            );

            log.info("🔍 DEBUG: PersonProfile criado, salvando no banco...");
            personProfileRepositoryPort.save(newPersonProfile);
            log.info("✅ PersonProfile criado com sucesso para usuário: {}", user.getId());

        } catch (Exception e) {
            log.error("❌ ERRO ao criar PersonProfile: {}", e.getMessage(), e);
            throw new ValidationException("Erro ao criar PersonProfile: " + e.getMessage());
        }
    }

    @Override
    public void updateProfile(UUID userId, Object profileData) {
        if (!(profileData instanceof PersonProfile personProfile)) {
            log.warn("Tipo de dados inválido para PersonProfile: {}", profileData.getClass());
            return;
        }

        log.info("Atualizando PersonProfile para usuário: {}", userId);
        // Buscar o perfil existente e atualizar os campos
        var existingProfile = personProfileRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new ValidationException("PersonProfile não encontrado para usuário: " + userId));

        // display_handle: validado e persistido em User (UserCommandService)

        // Atualizar campos mutáveis
        existingProfile.setFullName(personProfile.getFullName());
        existingProfile.setCpf(personProfile.getCpf());
        existingProfile.setRg(personProfile.getRg());
        existingProfile.setRgIssuer(personProfile.getRgIssuer());
        existingProfile.setRgState(personProfile.getRgState());
        existingProfile.setDateOfBirth(personProfile.getDateOfBirth());
        existingProfile.setGender(personProfile.getGender());
        existingProfile.setMaritalStatus(personProfile.getMaritalStatus());
        existingProfile.setNationality(personProfile.getNationality());
        existingProfile.setBirthCountry(personProfile.getBirthCountry());
        existingProfile.setBirthState(personProfile.getBirthState());
        existingProfile.setBirthCity(personProfile.getBirthCity());
        existingProfile.setMotherName(personProfile.getMotherName());
        existingProfile.setFatherName(personProfile.getFatherName());
        existingProfile.setPep(personProfile.isPep());
        existingProfile.setKycStatus(personProfile.getKycStatus());
        existingProfile.setKycLevel(personProfile.getKycLevel());
        existingProfile.setOccupation(personProfile.getOccupation());
        existingProfile.setIncomeRange(personProfile.getIncomeRange());

        personProfileRepositoryPort.save(existingProfile);
        log.info("PersonProfile atualizado com sucesso para usuário: {}", userId);
    }

    @Override
    public void deleteProfile(UUID userId) {
        log.info("Removendo PersonProfile para usuário: {}", userId);
        personProfileRepositoryPort.deleteByUserId(userId);
        log.info("PersonProfile removido com sucesso para usuário: {}", userId);
    }

    @Override
    public Object getProfile(UUID userId) {
        log.debug("Buscando PersonProfile para usuário: {}", userId);
        return personProfileRepositoryPort.findByUserId(userId).orElse(null);
    }
}
