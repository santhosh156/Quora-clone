package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUser(final String uuid) throws UserNotFoundException {

        UserEntity userEntity = userDao.getUserByUuid(uuid);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return userEntity;
    }
}
