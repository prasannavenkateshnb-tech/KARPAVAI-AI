package com.prepforge.entity;

import jakarta.persistence.*;

@Entity @Table(name="questions")
public class Question {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Company company;
 private int year; private String role, round, category, topic, difficulty, frequency, tags;
 @Column(nullable=false,length=4000) private String questionText;
 @Column(length=4000) private String expectedAnswer;
 @Column(length=5000) private String explanation;
 public Question(){}
 public Long getId(){return id;} public Company getCompany(){return company;} public int getYear(){return year;}
 public String getRole(){return role;} public String getRound(){return round;} public String getCategory(){return category;} public String getTopic(){return topic;}
 public String getDifficulty(){return difficulty;} public String getFrequency(){return frequency;} public String getTags(){return tags;}
 public String getQuestionText(){return questionText;} public String getExpectedAnswer(){return expectedAnswer;} public String getExplanation(){return explanation;}
 public void setCompany(Company v){company=v;} public void setYear(int v){year=v;} public void setRole(String v){role=v;} public void setRound(String v){round=v;}
 public void setCategory(String v){category=v;} public void setTopic(String v){topic=v;} public void setDifficulty(String v){difficulty=v;}
 public void setFrequency(String v){frequency=v;} public void setTags(String v){tags=v;} public void setQuestionText(String v){questionText=v;}
 public void setExpectedAnswer(String v){expectedAnswer=v;} public void setExplanation(String v){explanation=v;}
}
