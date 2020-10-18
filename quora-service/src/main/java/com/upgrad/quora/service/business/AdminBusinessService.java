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
public class AdminBusinessService {
    @Autowired
    private UserDao userDao;

    //call getUserAuthByAccessToken in Repository
    // verify access token that if its valid
    //verify the logout time of access token
    //verify that the signed in user is of role admin
    // verify that the user to be deleted exist
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(String userId, String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthByAccessToken = userDao.getUserAuthByAccessToken(authorization);
        if (userAuthByAccessToken==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        // if loggedout
        if( userAuthByAccessToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }
       UserEntity  signedInUser= userAuthByAccessToken.getUser();
        if (signedInUser.getRole().equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        UserEntity userToBeDeleted=userDao.getUserByUuid(userId);
        if(userToBeDeleted==null){
            throw  new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }
        userDao.deleteUser(userToBeDeleted);
    }


}
