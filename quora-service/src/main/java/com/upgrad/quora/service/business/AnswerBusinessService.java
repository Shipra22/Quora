package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDAO;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AnswerBusinessService {
    @Autowired
    private AnswerDAO answerDAO;
    @Autowired
    private UserDao userDao;
    @Autowired
    private QuestionDao questionDao;

    // createAnswer: this method creates an Answer for given question uuid
    // throws InvalidQuestionException if question not found in DB
    // throws AuthorizationFailedException if user has not signed in or already signed out
    // returns object of answerEntity created
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(String questionId, AnswerEntity answerEntity, String authorization) throws InvalidQuestionException, AuthorizationFailedException {
        QuestionEntity questionEntity = null;
        questionEntity= questionDao.getQuestionById(questionId);
        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }
        UserAuthEntity userAuthByAccessToken = userDao.getUserAuthByAccessToken(authorization);
        if (userAuthByAccessToken==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if( userAuthByAccessToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");
        }

        answerEntity.setUser(userAuthByAccessToken.getUser());
        answerEntity.setQuestion(questionEntity);
        return answerDAO.createAnswer(answerEntity);
    }

    // getAnswerByAnswerId: this method returns answerEntity object for given answer uuid
    // returns null if no object found with given uuid
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswerByAnswerId(String answerId)  {
        return answerDAO.getAnswerByAnswerId(answerId);
    }

    // editAnswer: this method edits the content of Answer for given answer uuid from DB
    // throws AnswerNotFoundException if answer not found in DB
    // throws AuthorizationFailedException if user has not signed in or already signed out or signed in user is not owner of the answer
    // returns object of answerEntity editted
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(String answerId, String answerContent, String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthByAccessToken = userDao.getUserAuthByAccessToken(authorization);
        if (userAuthByAccessToken==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if( userAuthByAccessToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit an answer");
        }
        AnswerEntity answerEntity = answerDAO.getAnswerByAnswerId(answerId);
        if(answerEntity == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        if(answerEntity.getUser() != userAuthByAccessToken.getUser()){
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
        }
        answerEntity.setAnswer(answerContent);
        answerEntity.setDate(new Date());
        answerDAO.editAnswer(answerEntity);
        return answerEntity;
    }

    // deleteAnswer: this method deletes the Answer for given answer uuid from DB
    // throws AnswerNotFoundException if answer not found in DB
    // throws AuthorizationFailedException if user has not signed in or already signed out or signed in user os neither admin nor owner of answer
    // returns object of answerEntity deleted
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String answerId, String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthByAccessToken = userDao.getUserAuthByAccessToken(authorization);
        if (userAuthByAccessToken==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if( userAuthByAccessToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");
        }
        AnswerEntity answerEntity = answerDAO.getAnswerByAnswerId(answerId);
        if(answerEntity == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        if(answerEntity.getUser() != userAuthByAccessToken.getUser() && !userAuthByAccessToken.getUser().getRole().equalsIgnoreCase("admin")){
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        }

        answerDAO.deleteAnswer(answerEntity);
        return answerEntity;
    }

    // deleteAnswer: this method returns the QuestionEntity for given question uuid from DB
    // throws InvalidQuestionException if question not found in DB
    // throws AuthorizationFailedException if user has not signed in or already signed out
    public QuestionEntity getAllAnswers(String questionId, String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthByAccessToken = userDao.getUserAuthByAccessToken(authorization);
        if (userAuthByAccessToken==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if( userAuthByAccessToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");
        }
        QuestionEntity questionEntity = null;
        questionEntity= questionDao.getQuestionById(questionId);
        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        }
        return questionEntity;
    }
}
