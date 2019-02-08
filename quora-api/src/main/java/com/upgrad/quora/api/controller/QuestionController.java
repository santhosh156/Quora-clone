package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {

        final QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());

        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity, accessToken);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionid}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionid") final String questionId,
                                                                 @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = null;
        QuestionDeleteResponse questionDeleteResponse = null;

        questionEntity = questionBusinessService.deleteQuestion(questionId, accessToken);

        if (questionEntity != null) {
            questionDeleteResponse = new QuestionDeleteResponse().id(questionEntity.getUuid()).
                    status("QUESTION DELETED");
            return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
        }
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.NOT_FOUND);
    }
}
