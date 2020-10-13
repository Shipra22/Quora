package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AuthenticationService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    //authenticate method take username and password as parameter
    //this method verify the credentials and generate a JWT access token
    //Call the getUserByUserName method in the passwordCryptographyProvider Service class
    // pass salt and password as parameters and generate encrypted password
    //  check if encrypted password matches the password obtained from the database
    // if false throw exception
    // if true, create a JWT token from the encrypted password
    // set user,uuid, current time expire time of the UserAuth entity
    // set the accesstoken field of UserAuth entity by JWT token
    // call  createAuthToken in Repository to store it in database
    // call UpdateUser in Repository to update user about the Last logout time of the user in the ‘UserEntity’

    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {

        UserEntity userEntity = userDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        //check if the password received matches with the password stored in the database
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthToken = new UserAuthEntity();
            userAuthToken.setUser(userEntity);
            userAuthToken.setUuid(UUID.randomUUID().toString());

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));

            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthToken);

            userDao.updateUser(userEntity);

            return userAuthToken;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");

        }
    }
}
