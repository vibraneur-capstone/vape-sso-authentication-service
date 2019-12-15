package com.vape.sso.service;

import com.vape.sso.model.UserModel;
import com.vape.sso.repository.credential.UserRepository;
import com.vape.sso.swagger.v1.model.TokenRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SaltService credentialSaltService;

    /**
     * retrieve a single user
     * @param id user
     * @return UserModel
     */
    UserModel getUserById(String id) {
        return userRepository.findUsersByClientId(id);
    }

    /**
     * Validate session request by comparing user credential
     * @param request TokenRequest
     * @return boolean
     */
    boolean validateTokenRequest(TokenRequest request){
        UserModel user = userRepository.findUsersByClientId(request.getClientId());
        return user != null && computeHash(user, request).equals(user.getHashedPwd());
    }

    /**
     * compute a hash for a user password
     * @param user userModel
     * @param request TokenRequest
     * @return String
     */
    private String computeHash(UserModel user, TokenRequest request) {
        String id = DigestUtils.sha256Hex(String.format("%s%s", user.getId(), user.getClientId()));
        String salt = credentialSaltService.getSaltByAssociateId(id);
        return salt == null ? "" : DigestUtils.sha256Hex(String.format("%s%s", salt, request.getClientSecret()));
    }
}
