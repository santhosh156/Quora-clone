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

}

