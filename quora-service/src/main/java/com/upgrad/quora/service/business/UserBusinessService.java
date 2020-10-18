package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private PasswordCryptographyProvider cryptographyProvider;

  public UserEntity getUserById(final String userUuid) throws UserNotFoundException {
    UserEntity userEntity = userDao.getUserById(userUuid);

    if(userEntity == null) {
      throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
    }

    return userEntity;
  }



  public UserAuthEntity getUserByToken(final String accessToken) throws AuthorizationFailedException {
    UserAuthEntity userAuthByToken = userDao.getUserAuthByToken(accessToken);

    if(userAuthByToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    if(userAuthByToken.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
    }

    return userAuthByToken;
  }


  public UserEntity getUserProfile(final String userUuid, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

    getUserByToken(accessToken);
    UserEntity userById = getUserById(userUuid);

    return userById;
  }


  @Transactional
  public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

      if(userDao.isUsernameExists(userEntity.getUsername())) {
        throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
      }

      if(userDao.isEmailExists(userEntity.getEmail())) {
        throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
      }

      String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
      userEntity.setSalt(encryptedText[0]);
      userEntity.setPassword(encryptedText[1]);

      UserEntity signUpUser = userDao.createUser(userEntity);

      return signUpUser;
  }




  public boolean isUserSignedIn(UserAuthEntity userAuthTokenEntity) {
    boolean isUserSignedIn = false;
    if (userAuthTokenEntity != null && userAuthTokenEntity.getLoginAt() != null && userAuthTokenEntity.getExpiresAt() != null) {
      if ((userAuthTokenEntity.getLogoutAt() == null)) {
        isUserSignedIn = true;
      }
    }
    return isUserSignedIn;
  }


  public boolean isUserAdmin(UserEntity user) {
    boolean isUserAdmin = false;
    if (user != null && "admin".equals(user.getRole())) {
      isUserAdmin = true;
    }
    return isUserAdmin;
  }
}
