package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public void editAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
    }

    public AnswerEntity getAnswerByAnswerId(String answerID) {
        try {
            AnswerEntity answerEntity = entityManager.createNamedQuery("answerByAnswerUid", AnswerEntity.class)
                    .setParameter("uuid", answerID)
                    .getSingleResult();
            return answerEntity;
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

}
