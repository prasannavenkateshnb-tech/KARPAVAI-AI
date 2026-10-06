package com.prepforge.entity;

import jakarta.persistence.*;

@Entity @Table(name="interview_experiences")
public class InterviewExperience {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Company company;
 private int year; private String role; private int rounds; private String difficulty, interviewType, selectionResult;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String questionsAsked;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String preparationTips;
 public InterviewExperience(){}
 public Long getId(){return id;} public Company getCompany(){return company;} public int getYear(){return year;} public String getRole(){return role;}
 public int getRounds(){return rounds;} public String getDifficulty(){return difficulty;} public String getInterviewType(){return interviewType;}
 public String getSelectionResult(){return selectionResult;} public String getDescription(){return description;} public String getQuestionsAsked(){return questionsAsked;} public String getPreparationTips(){return preparationTips;}
 public void setCompany(Company v){company=v;} public void setYear(int v){year=v;} public void setRole(String v){role=v;} public void setRounds(int v){rounds=v;}
 public void setDifficulty(String v){difficulty=v;} public void setInterviewType(String v){interviewType=v;} public void setSelectionResult(String v){selectionResult=v;}
 public void setDescription(String v){description=v;} public void setQuestionsAsked(String v){questionsAsked=v;} public void setPreparationTips(String v){preparationTips=v;}
}
