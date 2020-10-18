package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "question")
@NamedQueries({
        @NamedQuery(name = "getAllQuestions", query = "select q from QuestionEntity q"),
        @NamedQuery(name = "getQuestionById", query = "select q from QuestionEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "editQuestionById", query = "update QuestionEntity q set q.content = :content where q.uuid = :uuid"),
        @NamedQuery(name = "deleteQuestionById", query = "delete QuestionEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "getAllQuestionsByUser", query = "select q from QuestionEntity q where q.user = :user")
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
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  private List<AnswerEntity> answer;

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

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public List<AnswerEntity> getAnswer() {
    return answer;
  }

  public void setAnswer(List<AnswerEntity> answer) {
    this.answer = answer;
  }
}
