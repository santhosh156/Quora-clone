package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method=RequestMethod.POST, path="/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {

        final QuestionEntity questionEntity = new QuestionEntity();
        //get the bearerToken
        String[] bearerToken = accessToken.split("Bearer ");

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());

        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity, bearerToken[1]);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId})",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId,
                                                             @RequestHeader("authorization") final String authorizaton) throws AuthorizationFailedException, InvalidQuestionException {

        //Creating a new instance of the QuestionEntity
        QuestionEntity questionEntity = new QuestionEntity();
        //setQuestion with the new content from the questionEditRequest
        questionEntity.setContent(questionEditRequest.getContent());
        //get the bearerToken
        String[] bearerToken = authorizaton.split("Bearer ");
        //call the QuestionEditBusinessService to edit the question
        QuestionEntity editedQuestionEntity = questionBusinessService.editQuestion(questionEntity,questionId,bearerToken[1]);
        //attach the details to the questionEditResponse
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    //ResponseEntity GET method takes the accessToken as parameter and returns a list of question details for getAllQuestions endpoint
    @RequestMapping(method=RequestMethod.GET, path="/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity getAllQuestions(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException{
        //Bearer Authorization
        String[] bearerToken = accessToken.split("Bearer ");
        //getting the list of all questions using the bearertoken as parameter
        final List<QuestionEntity> getAllQuestions = questionBusinessService.getAllQuestions(bearerToken[1]);
        //adding the list of questions to the question detail response
        List<QuestionDetailsResponse> entities = new ArrayList<QuestionDetailsResponse>();
        for (QuestionEntity n : getAllQuestions) {
            QuestionDetailsResponse entity = new QuestionDetailsResponse();

            entity.setId( n.getUuid());
            entity.setContent(n.getContent());
            entities.add(entity);

        }
        //returning the response entity with the list of questions and httpstatus
        return  new ResponseEntity<>( entities,HttpStatus.OK);

    }

    //a controller method for getAllQuestionsbyUserid endpoint/////
    @RequestMapping(method=RequestMethod.GET, path="question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getAllQuestionsByUserId(@PathVariable("userId") final String userId, @RequestHeader("authorization")
    final String accessToken)throws UserNotFoundException, AuthorizationFailedException{

        //Bearer Authorization
        String[] bearerToken = accessToken.split("Bearer ");
        //getting the list of all questions whose userId is given and  using the bearertoken as parameter
        final List<QuestionEntity> getAllQuestions =questionBusinessService.getAllQuestionsByUserId(userId,bearerToken[1]);
        List<QuestionDetailsResponse> entities = new ArrayList<QuestionDetailsResponse>();
        //adding the list of questions to the question detail response
        for (QuestionEntity n : getAllQuestions) {
            QuestionDetailsResponse entity = new QuestionDetailsResponse();

            entity.setId( n.getUuid());
            entity.setContent(n.getContent());
            entities.add(entity);

        }
        //returning the response entity with the list of questions and httpstatus
        return  new ResponseEntity<>( entities,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionid") final String questionId,
                                                                 @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = null;
        QuestionDeleteResponse questionDeleteResponse = null;

        //Bearer Authorization
        String[] bearerToken = accessToken.split("Bearer ");

        questionEntity = questionBusinessService.deleteQuestion(questionId, bearerToken[1]);

        questionDeleteResponse = new QuestionDeleteResponse().id(questionEntity.getUuid()).
                    status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);

    }
}