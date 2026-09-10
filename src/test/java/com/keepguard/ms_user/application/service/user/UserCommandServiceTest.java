package com.keepguard.ms_user.application.service.user;

import com.keepguard.ms_user.application.dto.user.UserCreateCommandDTO;
import com.keepguard.ms_user.application.dto.user.UserDetailsViewDTO;
import com.keepguard.ms_user.application.dto.user.UserPatchPersonDocumentCommandDTO;
import com.keepguard.ms_user.application.dto.user.UserUpdateCommandDTO;
import com.keepguard.ms_user.application.mapper.UserApplicationMapper;
import com.keepguard.ms_user.application.port.out.cache.UserCachePort;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.port.out.persistence.PersonProfileRepositoryPort;
import com.keepguard.ms_user.application.port.out.persistence.UserRepositoryPort;
import com.keepguard.ms_user.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.application.service.exception.UnprocessableException;
import com.keepguard.ms_user.application.service.user.strategy.profile.ProfileStrategy;
import com.keepguard.ms_user.application.service.user.strategy.profile.ProfileStrategyFactory;
import com.keepguard.ms_user.domain.entity.PersonProfile;
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
import static org.mockito.Mockito.times;
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
    @Mock
    private com.keepguard.ms_user.infrastructure.messaging.UserErasureEventPublisher userErasureEventPublisher;

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

    @Test
    @DisplayName("Deve gravar CPF na primeira escrita")
    void shouldFirstWriteCpfOnPersonDocument() {
        User person = UserTestBuilder.builder().asPerson().asActive().buildDomainWithId();
        UUID profileId = UUID.randomUUID();
        PersonProfile profile = PersonProfile.of(
                profileId,
                person.getId(),
                "Nome Completo",
                null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                false, null, null, null, null,
                java.time.OffsetDateTime.now(),
                java.time.OffsetDateTime.now());
        UserPatchPersonDocumentCommandDTO command = new UserPatchPersonDocumentCommandDTO(
                person.getId(), person.getCompanyId(), VALID_CPF);

        when(userRepositoryPort.findByIdAndCompanyId(person.getId(), person.getCompanyId()))
                .thenReturn(Optional.of(person));
        when(personProfileRepositoryPort.findByUserId(person.getId())).thenReturn(Optional.of(profile));
        when(personProfileRepositoryPort.existsByCpfAndCompanyId(VALID_CPF, person.getCompanyId(), person.getId()))
                .thenReturn(false);
        when(personProfileRepositoryPort.save(profile)).thenReturn(profile);
        when(userApplicationMapper.toDetailsView(person, profile)).thenReturn(detailsView);

        UserDetailsViewDTO result = userCommandService.patchPersonDocument(command);

        assertThat(result).isEqualTo(detailsView);
        assertThat(profile.getId()).isEqualTo(profileId);
        assertThat(profile.getCpf()).isEqualTo(VALID_CPF);
        verify(personProfileRepositoryPort).save(profile);
        verify(personProfileRepositoryPort, times(1)).save(any(PersonProfile.class));
        verify(userCachePort).removeUserFromCache(person);
    }

    @Test
    @DisplayName("Deve recusar troca de CPF já gravado")
    void shouldRejectImmutablePersonDocument() {
        User person = UserTestBuilder.builder().asPerson().asActive().buildDomainWithId();
        PersonProfile profile = PersonProfile.create(person.getId(), "Nome Completo", VALID_CPF, null);
        UserPatchPersonDocumentCommandDTO command = new UserPatchPersonDocumentCommandDTO(
                person.getId(), person.getCompanyId(), "39053344705");

        when(userRepositoryPort.findByIdAndCompanyId(person.getId(), person.getCompanyId()))
                .thenReturn(Optional.of(person));
        when(personProfileRepositoryPort.findByUserId(person.getId())).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> userCommandService.patchPersonDocument(command))
                .isInstanceOf(AlreadyExistsException.class)
                .extracting("errorCode")
                .isEqualTo("PAYER_DOCUMENT_IMMUTABLE");

        verify(personProfileRepositoryPort, never()).save(any());
        verify(userCachePort, never()).removeUserFromCache(any());
    }

    @Test
    @DisplayName("Deve recusar CPF já usado por outro usuário da company")
    void shouldRejectCpfAlreadyUsedInCompany() {
        User person = UserTestBuilder.builder().asPerson().asActive().buildDomainWithId();
        PersonProfile profile = PersonProfile.create(person.getId(), "Nome Completo", null, null);
        UserPatchPersonDocumentCommandDTO command = new UserPatchPersonDocumentCommandDTO(
                person.getId(), person.getCompanyId(), VALID_CPF);

        when(userRepositoryPort.findByIdAndCompanyId(person.getId(), person.getCompanyId()))
                .thenReturn(Optional.of(person));
        when(personProfileRepositoryPort.findByUserId(person.getId())).thenReturn(Optional.of(profile));
        when(personProfileRepositoryPort.existsByCpfAndCompanyId(VALID_CPF, person.getCompanyId(), person.getId()))
                .thenReturn(true);

        assertThatThrownBy(() -> userCommandService.patchPersonDocument(command))
                .isInstanceOf(AlreadyExistsException.class)
                .extracting("errorCode")
                .isEqualTo("CPF_ALREADY_EXISTS");

        verify(personProfileRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve recusar CPF com checksum inválido")
    void shouldRejectInvalidCpfChecksum() {
        User person = UserTestBuilder.builder().asPerson().asActive().buildDomainWithId();
        PersonProfile profile = PersonProfile.create(person.getId(), "Nome Completo", null, null);
        UserPatchPersonDocumentCommandDTO command = new UserPatchPersonDocumentCommandDTO(
                person.getId(), person.getCompanyId(), "52998224726");

        when(userRepositoryPort.findByIdAndCompanyId(person.getId(), person.getCompanyId()))
                .thenReturn(Optional.of(person));
        when(personProfileRepositoryPort.findByUserId(person.getId())).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> userCommandService.patchPersonDocument(command))
                .isInstanceOf(UnprocessableException.class)
                .extracting("errorCode")
                .isEqualTo("PAYER_DOCUMENT_INVALID");

        verify(personProfileRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar 404 quando o usuário não pertence à company")
    void shouldReturnNotFoundWhenUserIsFromAnotherCompany() {
        User person = UserTestBuilder.builder().asPerson().asActive().buildDomainWithId();
        UUID otherCompany = UUID.randomUUID();
        UserPatchPersonDocumentCommandDTO command = new UserPatchPersonDocumentCommandDTO(
                person.getId(), otherCompany, VALID_CPF);

        when(userRepositoryPort.findByIdAndCompanyId(person.getId(), otherCompany))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCommandService.patchPersonDocument(command))
                .isInstanceOf(NotFoundException.class)
                .extracting("errorCode")
                .isEqualTo("USER_NOT_FOUND");

        verify(personProfileRepositoryPort, never()).save(any());
        verify(personProfileRepositoryPort, never()).findByUserId(any());
    }

    private static final String VALID_CPF = "52998224725";

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

    @Test
    @DisplayName("Deve anonimizar usuário e publicar user.erasure.requested ao deletar")
    void shouldAnonymizeUserAndPublishErasureRequestedOnDelete() {
        // Given
        when(userRepositoryPort.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        com.keepguard.ms_user.application.dto.user.UserDeleteCommandDTO deleteCommand =
                new com.keepguard.ms_user.application.dto.user.UserDeleteCommandDTO(user.getId(), user.getCompanyId());

        // When
        userCommandService.delete(deleteCommand);

        // Then
        assertThat(user.getStatus()).isEqualTo(com.keepguard.ms_user.domain.enums.UserStatusEnum.DELETED);
        assertThat(user.getEmail()).startsWith("anon_");
        assertThat(user.getEmail()).endsWith("@deleted.keepguard.local");
        assertThat(user.getPhoneE164()).isNull();
        assertThat(user.getAvatarUrl()).isNull();
        verify(userRepositoryPort).save(user);
        verify(userCachePort).removeUserFromCache(user);
        verify(userErasureEventPublisher).publishErasureRequested(any());
        verify(metricsPort).incrementCounter(eq("user_deleted_total"), any());
    }
}
