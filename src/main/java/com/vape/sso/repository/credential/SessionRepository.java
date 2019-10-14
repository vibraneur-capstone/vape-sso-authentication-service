package com.vape.sso.repository.credential;

import com.vape.sso.model.SessionModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SessionRepository extends MongoRepository<SessionModel, String> {
    @Query("{ 'userName': ?0 }")
    SessionModel findSessionModelByUserName(String userName);
}
