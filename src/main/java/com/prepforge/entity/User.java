package com.prepforge.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity @Table(name="users")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String email;
 @Column(nullable=false) private String password;
 @Column(nullable=false) private String name;
 @Column(nullable=false) private String role;
 private int solvedCount=0;
 private int mockTestsCompleted=0;
 private int experiencesRead=0;
 private int currentStreak=0;
 public User() {}
 public User(String name,String email,String password,String role){this.name=name;this.email=email;this.password=password;this.role=role;}
 public Long getId(){return id;} public String getEmail(){return email;} public String getPassword(){return password;}
 public String getName(){return name;} public String getRole(){return role;} public int getSolvedCount(){return solvedCount;}
 public int getMockTestsCompleted(){return mockTestsCompleted;} public int getExperiencesRead(){return experiencesRead;}
 public int getCurrentStreak(){return currentStreak;}
 public void setName(String v){name=v;} public void setEmail(String v){email=v;} public void setPassword(String v){password=v;}
 public void incrementSolved(){solvedCount++;} public void incrementMocks(){mockTestsCompleted++;} public void incrementExperiences(){experiencesRead++;}
 public void setCurrentStreak(int v){currentStreak=v;}
}
