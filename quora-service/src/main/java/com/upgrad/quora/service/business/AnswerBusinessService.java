package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
//The @Service annotation is used in your service layer.for auto-detection when using annotation-based configuration and classpath scanning.
@Service
public class AnswerBusinessService {
    //@Autowired,it basically injects a dependency
    @Autowired
    private UserDao userDao;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity,final String questionId,final String authorizationToken) throws InvalidQuestionException,AuthorizationFailedException {
        //First we need to check if the questionId exists in the db.So we use getQuestion method to get the details of the question.
        QuestionEntity questionEntity = questionDao.getQuestion(questionId);
        //here the authorizationToken is checked for authorization using userDao
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        //InvalidQuestionException is thrown if the questionId is null
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        //if the user is not signed in then it throws AuthorizationFailedException
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            //if the user is loggedout then this  AuthorizationFailedException is thrown
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        //all the attributes are set and sent to answerDao to create an answer in the database.
        final ZonedDateTime now = ZonedDateTime.now();
        answerEntity.setDate(now);
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setQuestion(questionEntity);
        return answerDao.createAnswer(answerEntity);
    }

    ////////editAnswer transactional method////////////////////////
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(AnswerEntity answerEntity,final String answerID, final String authorizationToken)throws  AuthorizationFailedException,AnswerNotFoundException{
        //get the userAuthToken details from userDao
        UserAuthTokenEntity userAuthTokenEntity =userDao.getUserAuthToken(authorizationToken);
        //get the answer Details using the answerId which is nothing but Uuid and put it in answerEntity1 instance
        AnswerEntity answerEntity1 =  answerDao.getAnswer(answerID);

        //throw AnswerNotFoundException if the answerId is not found in the database
        if(answerEntity1 == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        //Throw AuthorizationFailedException if the user is not authorized
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            //throw  AuthorizationFailedException if the user is logged out
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        //Throw AuthorizationFailedException if the owner of the answer and the ser who signed in are no the same
        else if(answerEntity1.getUser() != (userAuthTokenEntity.getUser())){
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        //now set the answer with the new answer content and attach i to the answerEntity1
        answerEntity1.setAnswer(answerEntity.getAnswer());
        //called answerDao to merge the content and update in the database
        answerDao.editAnswerContent(answerEntity1);
        return answerEntity1;
    }

    @Transactional
    public AnswerEntity deleteAnswer(final String answerId, final String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        AnswerEntity answerEntity=answerDao.getAnswerByUuid(answerId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        UserEntity userEntity = userAuthTokenEntity.getUser();

        if((userEntity == null) && (userEntity.getRole().equals("nonadmin"))){
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        }else if(answerEntity == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }


        return answerDao.deleteAnswer(answerEntity);
    }

    //a method which takes the questionId and accesstoken as the parameter for getAllAnswersToQuestion endpoint////////
    public List<AnswerEntity> getAllAnswersToQuestion(String questionId, final String accessToken)
            throws  AuthorizationFailedException, InvalidQuestionException {

        //check the user authorization using userDao
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        //get the user from the userAuthTokenEntity
        QuestionEntity questionEntity =  questionDao.getQuestion(questionId);

        //Throw UserNotFoundException if the user Uuid doesnt exit
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        //checking thr user authorization ,throws AuthorizationFailedException if the user is not signed in
        if (userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //Throws the AuthorizationFailedException ifthe user is loggedout
        else if (userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        //returns the list of answers created by user with userId
        return answerDao.getAllAnswersToQuestion(questionId);

    }


}
