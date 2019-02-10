package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerid}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerid") final String answerId,
                                                               @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = null;
        AnswerDeleteResponse answerDeleteResponse = null;

        answerEntity = answerBusinessService.deleteAnswer(answerId, accessToken);

        if (answerEntity != null) {
            answerDeleteResponse = new AnswerDeleteResponse().id(answerEntity.getUuid()).
                    status("ANSWER DELETED");
            return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
        }
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.NOT_FOUND);
    }
}
