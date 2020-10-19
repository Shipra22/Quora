package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBusinessService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private PasswordCryptographyProvider cryptographyProvider;

  public UserEntity getUserById(final String userUuid) throws UserNotFoundException {
    UserEntity userEntity = userDao.getUserByUuid(userUuid);

    if(userEntity == null) {
      throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
    }

    return userEntity;
  }



  public UserAuthEntity getUserByToken(final String accessToken) throws AuthorizationFailedException {
    UserAuthEntity userAuthByToken = userDao.getUserAuthByAccessToken(accessToken);

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
