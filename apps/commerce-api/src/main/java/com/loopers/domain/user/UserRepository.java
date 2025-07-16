package com.loopers.domain.user;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    UserModel save(UserModel userModel);

    boolean existsByUserId(String userId);

    UserModel getUserByUserId(String userId);

}
