package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
@Service
public class SignoutBusinessService {
    @Autowired
    private UserDao userDao;

    //signout method pass JWT access token as parameters
    //Call the getUserAuthByAccessToken method in the Repository and obtain a userAuth object with the corresponding access token
    //if access token doesnot exist in Database throw exception
    //set the logoutAt time of the UserAuth entity as the current time
    //Call the updateUserAuth method in the Repository to update the UserAuth Entity
   // return the userAuth entity
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signout(String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthByAccessToken = userDao.getUserAuthByAccessToken(accessToken);
        if (userAuthByAccessToken==null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }

        userAuthByAccessToken.setLogoutAt(ZonedDateTime.now());
        userDao.updateUserAuth(userAuthByAccessToken);
        return userAuthByAccessToken;
    }
}
