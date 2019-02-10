package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);
        questionEntity.setUser(userAuthTokenEntity.getUser());

        return questionDao.createQuestion(questionEntity);
    }

    @Transactional
    public QuestionEntity editQuestion (QuestionEntity questionEntity,final String questionID, final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        //get the userAuthToken details from userDao
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        //get the question Details using the questionId which is nothing but Uuid and put it in questionEntity instance
        QuestionEntity questionEntity1 =  questionDao.getQuestion(questionID);

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
        //now set the question with the new question content and attach i to the questionEntity1
        questionEntity1.setContent(questionEntity.getContent());

        //called questionDao to merge the content and update in the database
        questionDao.editQuestionContent(questionEntity1);
        return questionEntity1;

    }

    //A Method which takes the accessToken as parameter for authorization for getAllQuestions endpoint///////
    public List<QuestionEntity> getAllQuestions(final String accessToken) throws  AuthorizationFailedException {
        //checking the user authorization with the accesstoken
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        //Throw AuthorizationFailedException if the user has not signed in
        if (userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //Throw AuthorizationFailedException if the user is loggedout
        else if (userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        //return the all the questions from the database
        return questionDao.getAllQuestions();

    }

    //a method which takes the userid and accesstoken as the parameter for getallquestionsbyuserid endpoint////////
    public List<QuestionEntity> getAllQuestionsByUserId(String userId, final String accessToken) throws  AuthorizationFailedException,UserNotFoundException {
        //check the user authorization using userDao
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        //get the user from the userAuthTokenEntity
        UserEntity userEntity =  userDao.getUserByUuid(userId);

        //Throw UserNotFoundException if the user Uuid doesnt exit
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        //checking thr user authorization ,throws AuthorizationFailedException if the user is not signed in
        if (userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //Throws the AuthorizationFailedException ifthe user is loggedout
        else if (userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        //returns the list of questions created by user with userId
        return questionDao.getQuestionByUserId(userId);

    }

    @Transactional
    public QuestionEntity deleteQuestion(final String questionId, final String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        QuestionEntity questionEntity=questionDao.getQuestion(questionId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        UserEntity userEntity = userAuthTokenEntity.getUser();

        if(userEntity==null && userEntity.getRole().equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
        } else if(questionEntity==null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        return questionDao.deleteQuestion(questionEntity);
    }
}