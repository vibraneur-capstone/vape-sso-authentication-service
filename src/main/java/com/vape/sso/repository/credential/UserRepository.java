package com.vape.sso.repository.credential;

import com.vape.sso.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<UserModel, String> {

    @Query("{ 'clientId' : ?0 }")
    UserModel findUsersByClientId(String user);
}
