package com.keepguard.ms_user.domain.strategy;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.domain.entity.PersonProfile;

public class PersonUserStrategy implements UserProfileStrategy {

    @Override
    public void validateOnCreate(User user) {
        if (user.getType() != UserTypeEnum.PERSON) {
            throw new ValidationException("Estratégia PersonUserStrategy só pode ser usada para usuários do tipo PERSON");
        }

        // Validações básicas já feitas na entidade User
        // Aqui podemos adicionar validações específicas para PF se necessário

        // Validar se tem PersonProfile associado (será validado no UseCase)
        // Validar se email é único (será validado no UseCase)
        // Validar se CPF é único (será validado no UseCase)
    }

    @Override
    public void validateOnUpdate(User user) {
        if (user.getType() != UserTypeEnum.PERSON) {
            throw new ValidationException("Estratégia PersonUserStrategy só pode ser usada para usuários do tipo PERSON");
        }

        // Validações específicas para atualização de PF
        // Por exemplo: validar se mudanças são permitidas baseadas no status
    }

    @Override
    public void validateForActivation(User user) {
        if (user.getType() != UserTypeEnum.PERSON) {
            throw new ValidationException("Estratégia PersonUserStrategy só pode ser usada para usuários do tipo PERSON");
        }

        // Validações específicas para ativação de PF
        // Por exemplo: verificar se PersonProfile está completo
        // Verificar se KYC foi aprovado se necessário
    }

    @Override
    public UserTypeEnum getSupportedType() {
        return UserTypeEnum.PERSON;
    }

    public void validatePersonProfileForActivation(PersonProfile personProfile) {
        if (personProfile == null) {
            throw new ValidationException("Perfil de pessoa física é obrigatório para ativação");
        }

        if (personProfile.getFullName() == null || personProfile.getFullName().trim().isEmpty()) {
            throw new ValidationException("Nome completo é obrigatório para ativação");
        }

        if (personProfile.getCpf() == null || personProfile.getCpf().trim().isEmpty()) {
            throw new ValidationException("CPF é obrigatório para ativação");
        }

        if (personProfile.getDateOfBirth() == null) {
            throw new ValidationException("Data de nascimento é obrigatória para ativação");
        }

        if (!personProfile.isOfLegalAge()) {
            throw new ValidationException("Usuário deve ter pelo menos 18 anos para ativação");
        }
    }

    public void validatePersonProfileForCreation(PersonProfile personProfile) {
        if (personProfile == null) {
            throw new ValidationException("Perfil de pessoa física é obrigatório");
        }

        // Validações básicas já feitas na entidade PersonProfile
        // Aqui podemos adicionar validações específicas para criação
    }
}
