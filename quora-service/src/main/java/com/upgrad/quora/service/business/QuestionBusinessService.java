package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion (QuestionEntity questionEntity,final String questionID, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        //get the userAuthToken details from userDao
        UserAuthTokenEntity userAuthTokenEntity =userDao.getUserAuthToken(authorizationToken);
        //get the question Details using the questionId which is nothing but Uuid and put it in questionEntity instance
        QuestionEntity questionEntity1 =  questionDao.getQuestion(questionID);
        //now set the question with the new question content and attach i to the questionEntity1
        questionEntity1.setContent(questionEntity.getContent());
        //throw InvalidQuestionException if the questionId is not found in the database
        if(questionEntity1 == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }
        //Throw AuthorizationFailedException if the user is not authorized
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            //throw  AuthorizationFailedException if the user is logged out
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        //Throw AuthorizationFailedException if the owner of the question and the user who signed in are not the same
        else if(questionEntity1.getUser() != (userAuthTokenEntity.getUser())){
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        //called questionDao to merge the content and update in the database
        questionDao.editQuestionContent(questionEntity1);
        return  questionEntity;

    }
}
