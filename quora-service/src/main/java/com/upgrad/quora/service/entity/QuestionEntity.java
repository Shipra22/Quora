package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "question")
@NamedQueries({
        @NamedQuery(name = "getAllQuestions", query = "select q from QuestionEntity q"),
        @NamedQuery(name = "getQuestionById", query = "select q from QuestionEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "editQuestionById", query = "update QuestionEntity q set q.content = :content where q.uuid = :uuid"),
        @NamedQuery(name = "deleteQuestionById", query = "delete QuestionEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "getAllQuestionsByUser", query = "select q from QuestionEntity q where q.userId = :userId")
})
public class QuestionEntity implements Serializable {

  private static final long serialVersionUID = 6385994496663047405L;

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "UUID")
  @NotNull
  private String uuid;

  @Column(name = "CONTENT")
  @NotNull
  @Size(max = 500)
  private String content;

  @Column(name = "DATE")
  @NotNull
  private ZonedDateTime date;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private UserEntity userId;


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public UserEntity getUserId() {
    return userId;
  }

  public void setUserId(UserEntity userId) {
    this.userId = userId;
  }
}
