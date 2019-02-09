
package com.upgrad.quora.service.dao;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//@Repository is an annotation that marks the specific class as a Data Access Object
@Repository
public class AnswerDao {

   // @PersistenceContext,to inject an Entity Manager into their DAO classes
    @PersistenceContext
    private EntityManager entityManager;

    // a method to persist data in the database.
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }
}