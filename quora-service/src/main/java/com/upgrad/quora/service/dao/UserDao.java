package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    // create user in user table
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    // retrieve user by a using username
    public UserEntity getUserByUserName(String userName) {
        try {
            UserEntity userEntity = entityManager.createNamedQuery("userByUserName", UserEntity.class)
                    .setParameter("userName", userName)
                    .getSingleResult();
            return userEntity;
        } catch (Exception e) {
            return null;
        }
    }

    // retrieve user by a using uuid
    public UserEntity getUserByUuid(String uuid) {
        try {
            UserEntity userEntity = entityManager.createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return userEntity;
        } catch (Exception e) {
            return null;
        }
    }
  //  retrieve user by a using email
    public UserEntity getUserByEmail(String email) {
        try {
            UserEntity userEntity = entityManager.createNamedQuery("userByEmail", UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return userEntity;
        } catch (Exception e) {
            return null;
        }
    }

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthToken) {
        entityManager.persist(userAuthToken);
        return userAuthToken;
    }

    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

    public UserAuthEntity getUserAuthByAccessToken(String accessToken) {
        try {
            UserAuthEntity userAuthEntity = entityManager.createNamedQuery("userAuthByAcessToken", UserAuthEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
            return userAuthEntity;
        } catch (Exception e) {
            return null;
        }
    }

    public void updateUserAuth(final UserAuthEntity updatedUserAuthEntity) {
        entityManager.merge(updatedUserAuthEntity);
    }

    public void deleteUser(final UserEntity userEntity) {
        entityManager.remove(userEntity);

    }
}
