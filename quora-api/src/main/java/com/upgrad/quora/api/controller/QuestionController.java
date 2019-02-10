package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId})",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(final QuestionEditRequest questionEditRequest,@PathVariable("questionId") final String questionId,
                                                             @RequestHeader("authorization") final String authorizaton) throws AuthorizationFailedException, InvalidQuestionException {

        //Creating a new instance of the QuestionEntity
        QuestionEntity questionEntity = new QuestionEntity();
        //setQuestion with the new content from the questionEditRequest
        questionEntity.setContent(questionEditRequest.getContent());
        //get the bearerToken
        String[] bearerToken = authorizaton.split("Bearer ");
        //call the QuestionEditBusinessService to edit the question
        questionBusinessService.editQuestion(questionEntity,questionId,bearerToken[1]);
        //attach the details to the questionEditResponse
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}
