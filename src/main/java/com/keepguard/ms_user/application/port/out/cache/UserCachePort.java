package com.keepguard.ms_user.application.port.out.cache;

import com.keepguard.ms_user.application.dto.user.UserDetailsViewDTO;
import com.keepguard.ms_user.domain.entity.User;

public interface UserCachePort {

    // By Id
    void cacheUserById(String userId, UserDetailsViewDTO user);
    UserDetailsViewDTO getUserByIdFromCache(String userId);
    void removeUserFromCacheById(String userId);

    // By Email
    void cacheUserByEmail(String email, UserDetailsViewDTO user);
    UserDetailsViewDTO getUserByEmailFromCache(String email);
    void removeUserFromCacheByEmail(String email);

    // By CodeUser
    void cacheUserByCode(String codeUser, UserDetailsViewDTO user);
    UserDetailsViewDTO getUserByCodeFromCache(String codeUser);
    void removeUserFromCacheByCode(String codeUser);

    void removeUserFromCache(User user);
    void clearAllUserCache();

}
