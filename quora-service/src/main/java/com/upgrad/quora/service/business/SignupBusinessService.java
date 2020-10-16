package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SignupBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    //signup method pass UserEntity object as parameters
    //Call the getUserByUserName method in the Repository and obtain a user with the corresponding username in thedatabase
    //if user with that username already exist throw exception
    //Call the getUserByEmail method in the Repository and obtain a user with the corresponding email in thedatabase
    // if user with that email already exist throw exception
    //call PasswordCryptographyProvider Service to encrypt the password before storing in the database
    //Call the createUser method in the Repository to store the user in database

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity userByUserName = userDao.getUserByUserName(userEntity.getUserName());
        if (userByUserName != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        UserEntity userByEmail = userDao.getUserByEmail(userEntity.getEmail());
        if (userByEmail != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        //prefix basic keyword in the password
        //String BasicAuthentication= new String("Basic ").concat(encryptedText[1]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }


}
