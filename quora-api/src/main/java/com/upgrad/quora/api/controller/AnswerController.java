package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    private AnswerBusinessService answerBusinessService;

    // createAnswer method is mapped to '/question/{questionId}/answer/create'.It's POST method and  takes a questionId, AnswerRequest and authorization as parameters
    // call the business service to create answerEntity for a corresponding questionId
    // create the AnswerResponse instance with id = uuid of the answerEntity and status ANSWER CREATED
    // return the ResponseEntity of type AnswerResponse and
    // parameters as AnswerResponse object ,HttpStatus code CREATED
    @RequestMapping(path= "/question/{questionId}/answer/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionId,AnswerRequest answerRequest,@RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(new Date());

        answerEntity = answerBusinessService.createAnswer(questionId, answerEntity, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse,HttpStatus.CREATED);

    }

    // editAnswerContent method is mapped to '/answer/edit/{answerId}'.
    // call the business service to edit answerEntity for a corresponding answerid
    // create the AnswerEditResponse instance with id = uuid of the answerEntity and status ANSWER EDITED
    // return the ResponseEntity of type AnswerEditResponse and
    // parameters as AnswerEditResponse object ,HttpStatus code OK
    @RequestMapping(path= "/answer/edit/{answerId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent (@PathVariable("answerId") final String answerId, AnswerEditRequest answerEditRequest, @RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException, AnswerNotFoundException {
        String answerContent = answerEditRequest.getContent();

        AnswerEntity answerEntity = answerBusinessService.editAnswer(answerId, answerContent, authorization);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse,HttpStatus.OK);

    }

    // deleteAnswer: to delete an answer. only admin can use this endpoint
    // its a DELETE request and mapped to "/answer/delete/{answerId}" url
    // it requests the path variable 'answerId' of corresponding answer which is to be deleted and  access token of the signed in user
    //create UserDeleteResponse and set id and status field with uuid and "USER SUCCESSFULLY DELETED" respectively
    //return  UserDeleteResponse and HttpStatus.OK in ResponseEntity
    @RequestMapping(path= "/answer/delete/{answerId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer (@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerBusinessService.deleteAnswer(answerId, authorization);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);

    }

    @RequestMapping(path= "answer/all/{questionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion  (@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = answerBusinessService.getAllAnswers(questionId, authorization);
        String questionContent = questionEntity.getContent();
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<AnswerDetailsResponse>();
        for(AnswerEntity answerEntity: questionEntity.getAnswer()){
            AnswerDetailsResponse answerDetails = new AnswerDetailsResponse().id(answerEntity.getUuid()).answerContent(answerEntity.getAnswer()).questionContent(questionContent);
            answerDetailsResponses.add(answerDetails);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses,HttpStatus.OK);

    }
}
