package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    /*public UserEntity getUser(final String userEmail) {
        return userDao.getUser(userEmail);
    }*/
}
