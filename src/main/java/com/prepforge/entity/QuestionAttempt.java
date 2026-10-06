package com.prepforge.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="question_attempts")
public class QuestionAttempt {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private User user;
 @ManyToOne(optional=false) private Question question;
 private boolean solved; private boolean bookmarked; private LocalDateTime attemptedAt=LocalDateTime.now();
 public QuestionAttempt(){}
 public QuestionAttempt(User u,Question q,boolean solved){this.user=u;this.question=q;this.solved=solved;}
 public Long getId(){return id;} public User getUser(){return user;} public Question getQuestion(){return question;} public boolean isSolved(){return solved;}
 public boolean isBookmarked(){return bookmarked;} public void setSolved(boolean v){solved=v;} public void setBookmarked(boolean v){bookmarked=v;}
 public LocalDateTime getAttemptedAt(){return attemptedAt;}
}
