package com.keepguard.ms_user.application.port.out.persistence;

import com.keepguard.ms_user.domain.entity.Notify;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserNotifyRepositoryPort {

    Notify save(Notify notify);

    Optional<Notify> findByUserId(UUID userId);

    List<Notify> findAllByUserIdIn(List<UUID> userIds);

    void deleteByUserId(UUID userId);

    void delete(Notify notify);

    boolean existsByUserId(UUID userId);

    List<Notify> findAllByNotifyEmail(boolean notifyEmail);

    List<Notify> findAllByNotifySms(boolean notifySms);

    List<Notify> findAllByNotifyWhatsapp(boolean notifyWhatsapp);

    List<Notify> findAllByNotifyPush(boolean notifyPush);
}

