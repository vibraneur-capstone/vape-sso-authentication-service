package com.vape.sso.service;

import com.vape.sso.model.UserModel;
import com.vape.sso.repository.credential.UserRepository;
import com.vape.sso.swagger.model.SessionRequest;
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
     * Validate session request by comparing user credential
     * @param request SessionRequest
     * @return boolean
     */
    boolean validateSessionRequest(SessionRequest request){
        UserModel user = userRepository.findUsersByUser(request.getUser());
        return user != null && computeHash(user, request).equals(user.getHashedPwd());
    }

    /**
     * compute a hash for a user password
     * @param user userModel
     * @param request SessionRequest
     * @return String
     */
    private String computeHash(UserModel user, SessionRequest request) {
        String id = DigestUtils.sha256Hex(String.format("%s%s", user.getId(), user.getUser()));
        String salt = credentialSaltService.getSalt(id);
        return salt == null ? "" : DigestUtils.sha256Hex(String.format("%s%s", salt, request.getPwd()));
    }
}