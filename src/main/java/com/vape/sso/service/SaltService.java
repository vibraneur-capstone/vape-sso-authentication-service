package com.vape.sso.service;

import com.vape.sso.repository.credentialSalt.CredentialSaltRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaltService {

    @Autowired
    private CredentialSaltRepository credentialSaltRepository;

    String getSalt(String associateId) {
        return credentialSaltRepository.findCredentialSaltsByAssociateId(associateId).getSalt();
    }
}
