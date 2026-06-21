package com.keepguard.ms_user.domain.strategy;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.domain.entity.CompanyProfile;

public class CompanyUserStrategy implements UserProfileStrategy {

    @Override
    public void validateOnCreate(User user) {
        if (user.getType() != UserTypeEnum.COMPANY) {
            throw new ValidationException("Estratégia CompanyUserStrategy só pode ser usada para usuários do tipo COMPANY");
        }

        // Validações básicas já feitas na entidade User
        // Aqui podemos adicionar validações específicas para PJ se necessário

        // Validar se tem CompanyProfile associado (será validado no UseCase)
        // Validar se companyId existe (será validado no UseCase via CompanyDirectoryPort)
        // Validar se CNPJ é único se informado (será validado no UseCase)
    }

    @Override
    public void validateOnUpdate(User user) {
        if (user.getType() != UserTypeEnum.COMPANY) {
            throw new ValidationException("Estratégia CompanyUserStrategy só pode ser usada para usuários do tipo COMPANY");
        }

        // Validações específicas para atualização de PJ
        // Por exemplo: validar se mudanças são permitidas baseadas no status
    }

    @Override
    public void validateForActivation(User user) {
        if (user.getType() != UserTypeEnum.COMPANY) {
            throw new ValidationException("Estratégia CompanyUserStrategy só pode ser usada para usuários do tipo COMPANY");
        }

        // Validações específicas para ativação de PJ
        // Por exemplo: verificar se CompanyProfile está completo
        // Verificar se empresa existe e está ativa
    }

    @Override
    public UserTypeEnum getSupportedType() {
        return UserTypeEnum.COMPANY;
    }

    public void validateCompanyProfileForActivation(CompanyProfile companyProfile) {
        if (companyProfile == null) {
            throw new ValidationException("Perfil de pessoa jurídica é obrigatório para ativação");
        }

        if (companyProfile.getCompanyId() == null) {
            throw new ValidationException("ID da empresa é obrigatório para ativação");
        }

        // Para PJ, o companyId é obrigatório e deve existir no ms-company
        // A validação de existência será feita no UseCase via CompanyDirectoryPort
    }

    public void validateCompanyProfileForCreation(CompanyProfile companyProfile) {
        if (companyProfile == null) {
            throw new ValidationException("Perfil de pessoa jurídica é obrigatório");
        }

        if (companyProfile.getCompanyId() == null) {
            throw new ValidationException("ID da empresa é obrigatório");
        }

        // Validações básicas já feitas na entidade CompanyProfile
        // Aqui podemos adicionar validações específicas para criação
    }

    public void validateRepresentativeInfo(CompanyProfile companyProfile) {
        if (companyProfile == null) return;

        // Se informou nome do representante, deve informar CPF também
        if (companyProfile.getRepresentativeName() != null && !companyProfile.getRepresentativeName().trim().isEmpty()) {
            if (companyProfile.getRepresentativeCpf() == null || companyProfile.getRepresentativeCpf().trim().isEmpty()) {
                throw new ValidationException("CPF do representante é obrigatório quando nome é informado");
            }
        }

        // Se informou CPF do representante, deve informar nome também
        if (companyProfile.getRepresentativeCpf() != null && !companyProfile.getRepresentativeCpf().trim().isEmpty()) {
            if (companyProfile.getRepresentativeName() == null || companyProfile.getRepresentativeName().trim().isEmpty()) {
                throw new ValidationException("Nome do representante é obrigatório quando CPF é informado");
            }
        }
    }
}
