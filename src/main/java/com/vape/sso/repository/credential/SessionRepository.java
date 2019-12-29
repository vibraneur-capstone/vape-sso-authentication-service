package com.vape.sso.repository.credential;

import com.vape.sso.model.TokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SessionRepository extends MongoRepository<TokenModel, String> {
    @Query("{ 'user': ?0 }")
    TokenModel findSessionModelByUser(String userName);
}
