package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsBusinessService {
    @Autowired
    private UserDao userDao;

    //userDetail take userId and authorization(access token) as parameters
    //calls getUserAuthByAccessToken() in Repository class to verify access token of the signed in user
    //check the logout time of the access token
    //call getUserByUuid in Repository class to verify that the user whose details is needed exist
    //return the user
    @Transactional(propagation = Propagation.REQUIRED)
     public UserEntity userDetails(String userId, String authorization) throws AuthorizationFailedException, UserNotFoundException {
         UserAuthEntity userAuthByAccessToken = userDao.getUserAuthByAccessToken(authorization);
       if (userAuthByAccessToken==null){
           throw new AuthorizationFailedException("ATHR-001","User has not signed in");
       }
       // if loggedout
         if( userAuthByAccessToken.getLogoutAt() != null){
             throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
         }
         //if uuid not found
         final UserEntity userEntity = userDao.getUserByUuid(userId);

         if(userEntity== null){
             throw  new UserNotFoundException("USR-001","User with entered uuid does not exist");
         }
         return userEntity;
     }

}
