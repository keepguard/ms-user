package com.keepguard.ms_user.application.port.in;

import com.keepguard.ms_user.application.dto.register.RegisterInitCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterInitViewDTO;
import com.keepguard.ms_user.application.dto.register.RegisterConfirmCommandDTO;
import com.keepguard.ms_user.application.dto.register.RegisterResendCommandDTO;
import com.keepguard.ms_user.domain.entity.RegisterSession;

public interface RegisterPort {

    /**
     * Inicializa o processo de registro de um novo usuário.
     * Valida os dados, gera token de verificação e armazena no Redis.
     *
     * @param command Dados para inicialização do registro
     * @return View com dados da sessão de registro
     */
    RegisterInitViewDTO init(RegisterInitCommandDTO command);

    /**
     * Confirma o registro de um novo usuário.
     * Valida o token e remove a sessão do Redis.
     *
     * @param command Dados para confirmação do registro
     * @return Sessão de registro confirmada com todos os dados
     */
    RegisterSession confirm(RegisterConfirmCommandDTO command);

    /**
     * Reenvia o token de registro.
     * Valida a sessão e incrementa contador de reenvios.
     *
     * @param command Dados para reenvio do token
     * @return Sessão de registro com dados atualizados
     */
    RegisterSession resend(RegisterResendCommandDTO command);
}

