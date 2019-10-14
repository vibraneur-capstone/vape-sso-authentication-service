package com.vape.sso.service;

import com.vape.sso.repository.credentialSalt.CredentialSaltRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaltService {

    @Autowired
    private CredentialSaltRepository credentialSaltRepository;

    String getSalt(String associateId) {
        String a = credentialSaltRepository.findCredentialSaltsByAssociateId(associateId).getSalt();
        System.out.println(a);
        return a;
    }
}
