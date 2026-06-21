package com.keepguard.ms_user.application.port.out.cache;

import com.keepguard.ms_user.application.dto.notify.NotifyViewDTO;

public interface NotifyCachePort {

    // By UserId
    void cacheNotifyByUserId(String userId, NotifyViewDTO notify);
    NotifyViewDTO getNotifyByUserIdFromCache(String userId);
    void removeNotifyFromCacheByUserId(String userId);

    // Clear All
    void clearAllNotifyCache();

}
