package com.vape.sso.repository.credential;

import com.vape.sso.model.SessionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<SessionModel, String> {

}
