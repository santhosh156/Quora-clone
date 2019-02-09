package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    ////////////A Method which takes the accessToken as parameter for authorization for getAllQuestions endpoint///////
    public List<QuestionEntity> getAllQuestions(final String accessToken) throws  AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if (userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        return questionDao.getAllQuestions();

    }
/////////////////a method which takes the userid and accesstoken as the parameter for getallquestionsbyuserid endpoint////////
    public List<QuestionEntity> getAllQuestionsByUserId(String userId,final String accessToken) throws  AuthorizationFailedException,UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        UserEntity userEntity=  userAuthTokenEntity.getUser();
        //String userUuid =userEntity.getUuid();
        //  UserEntity userEntityUuid = userDao.getUserByUuid(userId);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }


        if (userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }
        return questionDao.getQuestionByUserId(userId);

    }
}
