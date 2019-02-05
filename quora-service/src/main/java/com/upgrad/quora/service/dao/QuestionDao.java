package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }
/////////////////////getting the list of questions for getAllQuestions endpoint/////////////////////////
    public List<QuestionEntity> getAllQuestions(){

        return entityManager.createNamedQuery("AllQuestions", QuestionEntity.class).getResultList();
    }

}
