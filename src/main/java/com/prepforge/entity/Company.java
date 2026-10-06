package com.prepforge.entity;

import jakarta.persistence.*;

@Entity @Table(name="companies")
public class Company {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String name;
 @Column(length=2000) private String description;
 private String website, industry, difficulty, logoUrl;
 @Column(length=2000) private String recruitmentProcess;
 public Company() {}
 public Company(String name,String description,String website,String industry,String difficulty,String logoUrl,String recruitmentProcess){
  this.name=name;this.description=description;this.website=website;this.industry=industry;this.difficulty=difficulty;this.logoUrl=logoUrl;this.recruitmentProcess=recruitmentProcess;
 }
 public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;}
 public String getWebsite(){return website;} public String getIndustry(){return industry;} public String getDifficulty(){return difficulty;}
 public String getLogoUrl(){return logoUrl;} public String getRecruitmentProcess(){return recruitmentProcess;}
 public void setName(String v){name=v;} public void setDescription(String v){description=v;} public void setWebsite(String v){website=v;}
 public void setIndustry(String v){industry=v;} public void setDifficulty(String v){difficulty=v;} public void setLogoUrl(String v){logoUrl=v;}
 public void setRecruitmentProcess(String v){recruitmentProcess=v;}
}
