package com.prepforge.config;
import com.prepforge.entity.*; import com.prepforge.repository.*; import org.springframework.boot.CommandLineRunner; import org.springframework.context.annotation.*; import org.springframework.security.crypto.password.PasswordEncoder; import java.util.*;
@Configuration public class DataSeeder {
 @Bean CommandLineRunner seed(UserRepository users,CompanyRepository companies,QuestionRepository questions,InterviewExperienceRepository exps,PasswordEncoder enc){
  return args->{ if(users.count()==0){users.save(new User("Admin","admin@prepforge.com",enc.encode("Admin@123"),"ADMIN"));users.save(new User("Demo Student","student@prepforge.com",enc.encode("Student@123"),"STUDENT"));} 
   if(companies.count()==0){
    String[] names={"Amazon","Microsoft","Google","TCS","Infosys","Wipro","Accenture","Cognizant","IBM","Capgemini","Deloitte","Zoho","Freshworks","JPMorgan","Oracle"};
    for(String n:names)companies.save(new Company(n,n+" placement preparation profile.","https://www."+n.toLowerCase()+".com","Technology","Medium","", "Aptitude → Coding → Technical → HR"));
   }
   if(questions.count()==0){List<Company> cs=companies.findAll();String[][] data={{"DSA","Arrays","Find the maximum subarray sum."},{"Java","OOP","Explain polymorphism with an example."},{"SQL","Joins","Explain INNER JOIN and LEFT JOIN."},{"DBMS","Transactions","What are ACID properties?"},{"Operating Systems","Processes","What is the difference between process and thread?"},{"Computer Networks","HTTP","Explain HTTP request and response."},{"Aptitude","Percentages","Solve a percentage change problem."},{"HR","Behavioral","Tell me about yourself."},{"System Design","Scalability","What is horizontal scaling?"}};
    int y=2017; for(int i=0;i<cs.size();i++){for(int j=0;j<data.length;j++){Question q=new Question();q.setCompany(cs.get(i));q.setYear(y+(i+j)%10);q.setRole("Software Developer");q.setRound(j%3==0?"Coding":"Technical Interview");q.setCategory(data[j][0]);q.setTopic(data[j][1]);q.setDifficulty(j%3==0?"Medium":"Easy");q.setQuestionText(data[j][2]);q.setExpectedAnswer("Demo answer for study and viva demonstration.");q.setExplanation("Seed/demo content. Verify historical claims before presenting as authentic.");q.setFrequency(j<4?"Frequently Asked":"Occasionally Asked");q.setTags(data[j][0]+",placement");questions.save(q);}}
   }
   if(exps.count()==0){for(Company c:companies.findAll().subList(0,Math.min(5,(int)companies.count()))){InterviewExperience x=new InterviewExperience();x.setCompany(c);x.setYear(2025);x.setRole("Software Developer");x.setRounds(4);x.setDifficulty("Medium");x.setInterviewType("Campus");x.setSelectionResult("Demo");x.setDescription("Seed/demo interview experience for project demonstration.");x.setQuestionsAsked("Coding, OOP, SQL, HR");x.setPreparationTips("Practice consistently and verify company-specific information.");exps.save(x);}}
  };
 }
}
