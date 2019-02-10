
package com.upgrad.quora.service.dao;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

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

    //this method get the answer details using the Uuid of the answer
    public AnswerEntity getAnswer(String answerId){
        try {
            return entityManager.createNamedQuery("AnswersDetails", AnswerEntity.class).setParameter("answerId",answerId)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    // a method to update the and merge the new data in the database
    public void editAnswerContent(final AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
    }

    // Method to delete the answer in database
    public AnswerEntity deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("AnswersDetails", AnswerEntity.class).
                    setParameter("uuid", uuid)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    //getting the list of questions for getAllQuestionsuserid endpoint////////////////
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId) {
        try {
            TypedQuery<AnswerEntity> query = entityManager.createNamedQuery("AllAnswers", AnswerEntity.class);

            List<AnswerEntity> answerList = query.getResultList();
            List<AnswerEntity> resultList = new ArrayList<AnswerEntity>();
            //this for loop selects all the comments whose imageId equals the comment's imageid
            for(AnswerEntity answer : answerList){
                if(answer.getQuestion().getUuid().equals(questionId)){
                    //add the question to the list
                    resultList.add(answer);
                }
            }

            return resultList;
        } catch(NoResultException nre) {
            return null;
        }
    }
}