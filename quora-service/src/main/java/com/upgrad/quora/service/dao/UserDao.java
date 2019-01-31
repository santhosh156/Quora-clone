package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import jdk.nashorn.internal.objects.annotations.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /*public UserEntity getUser(final String userUuid) {
        entityManager.createNamedQuery("userByEmail")
        return userEntity;
    }*/
}
