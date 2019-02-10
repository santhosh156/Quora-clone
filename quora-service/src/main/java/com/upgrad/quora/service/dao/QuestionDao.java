package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public void editQuestionContent(final QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

    public QuestionEntity getQuestion(final String uuid) {
        try {


            return entityManager.createNamedQuery("getQuestion", QuestionEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        }   catch (NoResultException nre) {
            return null;
        }
    }
}