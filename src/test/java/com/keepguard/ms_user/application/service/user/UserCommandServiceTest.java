package com.keepguard.ms_user.application.service.user;

import com.keepguard.ms_user.application.dto.user.UserCreateCommandDTO;
import com.keepguard.ms_user.application.dto.user.UserDetailsViewDTO;
import com.keepguard.ms_user.application.dto.user.UserUpdateCommandDTO;
import com.keepguard.ms_user.application.mapper.UserApplicationMapper;
import com.keepguard.ms_user.application.port.out.cache.UserCachePort;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.port.out.persistence.PersonProfileRepositoryPort;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_user.application.service.user.strategy.profile.ProfileStrategy;
import com.keepguard.ms_user.application.service.user.strategy.profile.ProfileStrategyFactory;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.test.builder.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandService - Unicidade por company")
class UserCommandServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private ProfileStrategyFactory profileStrategyFactory;
    @Mock
    private UserCachePort userCachePort;
    @Mock
    private UserApplicationMapper userApplicationMapper;
    @Mock
    private MetricsPort metricsPort;
    @Mock
    private PersonProfileRepositoryPort personProfileRepositoryPort;
    @Mock
    private ProfileStrategy profileStrategy;

    @InjectMocks
    private UserCommandService userCommandService;

    private User user;
    private UserCreateCommandDTO createCommand;
    private UserDetailsViewDTO detailsView;

    @BeforeEach
    void setUp() {
        var builder = UserTestBuilder.builder().asPerson().asActive();
        user = builder.buildDomain();
        createCommand = builder.buildCreateCommand();
        detailsView = builder.buildDetailsView();
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar com email já existente na company")
    void shouldThrowWhenEmailAlreadyExistsInCompanyOnCreate() {
        when(userRepositoryPort.existsByEmailAndCompanyId(createCommand.email(), createCommand.companyId(), null))
                .thenReturn(true);

        assertThatThrownBy(() -> userCommandService.create(createCommand))
                .isInstanceOf(AlreadyExistsException.class)
                .extracting("errorCode")
                .isEqualTo("EMAIL_ALREADY_EXISTS");

        verify(userRepositoryPort).existsByEmailAndCompanyId(createCommand.email(), createCommand.companyId(), null);
        verify(userRepositoryPort, never()).existsByEmail(anyString());
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar com telefone já existente na company")
    void shouldThrowWhenPhoneAlreadyExistsInCompanyOnCreate() {
        when(userRepositoryPort.existsByEmailAndCompanyId(createCommand.email(), createCommand.companyId(), null))
                .thenReturn(false);
        when(userRepositoryPort.existsByPhoneE164AndCompanyId(createCommand.phoneE164(), createCommand.companyId(), null))
                .thenReturn(true);

        assertThatThrownBy(() -> userCommandService.create(createCommand))
                .isInstanceOf(AlreadyExistsException.class)
                .extracting("errorCode")
                .isEqualTo("PHONE_ALREADY_EXISTS");

        verify(userRepositoryPort).existsByPhoneE164AndCompanyId(createCommand.phoneE164(), createCommand.companyId(), null);
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar usuário quando email e telefone não existem nesta company")
    void shouldCreateWhenEmailAndPhoneAreFreeInCompany() {
        when(userRepositoryPort.existsByEmailAndCompanyId(createCommand.email(), createCommand.companyId(), null))
                .thenReturn(false);
        when(userRepositoryPort.existsByPhoneE164AndCompanyId(createCommand.phoneE164(), createCommand.companyId(), null))
                .thenReturn(false);
        when(userRepositoryPort.existsByDisplayHandleAndCompanyId(anyString(), eq(createCommand.companyId()), any()))
                .thenReturn(false);
        when(userApplicationMapper.toDomain(createCommand)).thenReturn(user);
        when(userRepositoryPort.save(any(User.class))).thenReturn(user);
        when(userApplicationMapper.toDetailsView(user)).thenReturn(detailsView);

        UserDetailsViewDTO result = userCommandService.create(createCommand);

        assertThat(result).isEqualTo(detailsView);
        verify(userRepositoryPort).existsByEmailAndCompanyId(createCommand.email(), createCommand.companyId(), null);
        verify(userRepositoryPort, never()).existsByEmail(anyString());
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar para email já usado por outro usuário da company")
    void shouldThrowWhenUpdatingToEmailTakenInCompany() {
        String newEmail = "outro@example.com";
        when(userRepositoryPort.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepositoryPort.existsByEmailAndCompanyId(newEmail, user.getCompanyId(), user.getId()))
                .thenReturn(true);

        UserUpdateCommandDTO command = updateCommand(Optional.of(newEmail), Optional.empty());

        assertThatThrownBy(() -> userCommandService.update(command))
                .isInstanceOf(AlreadyExistsException.class)
                .extracting("errorCode")
                .isEqualTo("EMAIL_ALREADY_EXISTS");

        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir atualizar para o próprio email e telefone")
    void shouldAllowUpdatingToOwnEmailAndPhone() {
        when(userRepositoryPort.findById(user.getId())).thenReturn(Optional.of(user));
        when(userApplicationMapper.applyChanges(eq(user), any())).thenReturn(user);
        when(userRepositoryPort.save(user)).thenReturn(user);
        when(profileStrategyFactory.getStrategy(user.getType())).thenReturn(profileStrategy);
        when(userApplicationMapper.toDetailsView(user)).thenReturn(detailsView);

        UserUpdateCommandDTO command = updateCommand(Optional.of(user.getEmail()), Optional.of(user.getPhoneE164()));

        UserDetailsViewDTO result = userCommandService.update(command);

        assertThat(result).isEqualTo(detailsView);
        verify(userRepositoryPort, never()).existsByEmailAndCompanyId(anyString(), any(), any());
        verify(userRepositoryPort, never()).existsByPhoneE164AndCompanyId(anyString(), any(), any());
        verify(userRepositoryPort).save(user);
    }

    private UserUpdateCommandDTO updateCommand(Optional<String> email, Optional<String> phone) {
        return new UserUpdateCommandDTO(
                user.getId(),
                user.getCompanyId(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                email,
                phone,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }
}
