package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//The annotation which adds the @Controller.The RESTful web service controller simply returns the object
// and the object data is written directly to the HTTP response as JSON/XML.
@RestController
//The annotation to map with URL request of type '/'
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    //An annotation to map this method with Http request of POST type, map it with URL request of type '/question/{questionId}/answer/create' and it produces and consumes Json
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId,
                                                       @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        //An instance of AnswerEntity is created which sets the Answer Content from the Answer Request.
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        //The accessToken is split to as Bearer and the actual AuthorizationToken
        String[] bearerToken = accessToken.split("Bearer ");
        //Here the answerBusinessService is calls createAnswer method where it takes in the answerEntity,questionId and the BearerToken as parameters
        answerBusinessService.createAnswer(answerEntity, questionId, bearerToken[1]);
        //after persist.The answerResponse gets the new Uuid of the  Answer created
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        //returns the answerResponse with HTTP status
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);

    }

    //Creating a method for editing an already existing answer ////////////////////////////////////////////
    @RequestMapping(method=RequestMethod.PUT, path="/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(final AnswerEditRequest answerEditRequest,@PathVariable("answerId") final String answerId,
                                                         @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        //Creating a new instance of the AnswerEntity
        AnswerEntity answerEntity = new AnswerEntity();

        //setAnswer with the new content from the ansEditRequest
        answerEntity.setAnswer(answerEditRequest.getContent());

        //get the bearerToken
        String[] bearerToken = accessToken.split("Bearer ");

        //call the answerBusinessService to the edit the answer
        AnswerEntity editedAnswerEntity = answerBusinessService.editAnswer(answerEntity,answerId,bearerToken[1]);

        //attach the details to the answerEditResponse
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(editedAnswerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerid") final String answerId,
                                                             @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = null;
        AnswerDeleteResponse answerDeleteResponse = null;

        //get the bearerToken
        String[] bearerToken = accessToken.split("Bearer ");

        answerEntity = answerBusinessService.deleteAnswer(answerId, bearerToken[1]);

        answerDeleteResponse = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);

    }

    //a controller method for getAllAnswersToQuestion endpoint/////
    @RequestMapping(method=RequestMethod.GET, path="answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getAllAnswersToQuestion (@PathVariable("questionId") final String questionId, @RequestHeader("authorization")
                                                final String accessToken)throws InvalidQuestionException, AuthorizationFailedException{

        //Bearer Authorization
        String[] bearerToken = accessToken.split("Bearer ");

        //getting the list of all answers whose questionId is given and  using the bearertoken as parameter
        final List<AnswerEntity> getAllAnswers = answerBusinessService.getAllAnswersToQuestion(questionId, bearerToken[1]);
        List<AnswerDetailsResponse> entities = new ArrayList<AnswerDetailsResponse>();

        //adding the list of answers to the answers detail response
        for (AnswerEntity n : getAllAnswers) {
            AnswerDetailsResponse entity = new AnswerDetailsResponse();

            entity.setId(n.getUuid());
            entity.setAnswerContent(n.getAnswer());
            entity.setQuestionContent(n.getQuestion().getContent());
            entities.add(entity);
        }

        //returning the response entity with the list of questions and httpstatus
        return  new ResponseEntity<>( entities, HttpStatus.OK);
    }

}