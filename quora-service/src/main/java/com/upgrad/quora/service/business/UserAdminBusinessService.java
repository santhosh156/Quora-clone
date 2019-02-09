package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUser(final String uuid, final String accessToken) throws UserNotFoundException, AuthorizationFailedException {

        UserEntity userEntity = userDao.getUserByUuid(uuid);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        return userEntity;
    }


    public UserEntity deleteUser(final String authorization,final String userUuid)throws UserNotFoundException, AuthorizationFailedException {
        UserEntity userEntity = userDao.getUserByUuid(userUuid);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userEntity.getRole().equals("admin")) {
            if (userAuthTokenEntity == null) {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            } else if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            } else  {
                userDao.deleteUser(userEntity);
            }
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        return userEntity;

    }



}
