package com.keepguard.ms_user.infrastructure.persistence.spring;

import com.keepguard.ms_user.infrastructure.persistence.entity.NotifyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotifySpringRepository extends JpaRepository<NotifyJpaEntity, UUID> {

    @Query("SELECT n FROM NotifyJpaEntity n WHERE n.userId = :userId")
    Optional<NotifyJpaEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT n FROM NotifyJpaEntity n WHERE n.userId IN :userIds")
    List<NotifyJpaEntity> findAllByUserIdIn(@Param("userIds") List<UUID> userIds);

    boolean existsByUserId(UUID userId);

    @Query("SELECT n FROM NotifyJpaEntity n WHERE n.notifyEmail = :notifyEmail")
    List<NotifyJpaEntity> findAllByNotifyEmail(@Param("notifyEmail") boolean notifyEmail);

    @Query("SELECT n FROM NotifyJpaEntity n WHERE n.notifySms = :notifySms")
    List<NotifyJpaEntity> findAllByNotifySms(@Param("notifySms") boolean notifySms);

    @Query("SELECT n FROM NotifyJpaEntity n WHERE n.notifyWhatsapp = :notifyWhatsapp")
    List<NotifyJpaEntity> findAllByNotifyWhatsapp(@Param("notifyWhatsapp") boolean notifyWhatsapp);

    @Query("SELECT n FROM NotifyJpaEntity n WHERE n.notifyPush = :notifyPush")
    List<NotifyJpaEntity> findAllByNotifyPush(@Param("notifyPush") boolean notifyPush);

    @Query("DELETE FROM NotifyJpaEntity n WHERE n.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
