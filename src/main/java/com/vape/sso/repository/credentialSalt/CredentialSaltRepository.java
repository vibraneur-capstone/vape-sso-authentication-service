package com.vape.sso.repository.credentialSalt;

import com.vape.sso.model.CredentialSaltModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CredentialSaltRepository extends MongoRepository<CredentialSaltModel, String> {

    @Query("{ 'associateId': ?0 }")
    CredentialSaltModel findCredentialSaltsByAssociateId(String associateId);
}
