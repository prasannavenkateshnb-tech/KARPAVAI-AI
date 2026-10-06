package com.prepforge.controller;
import com.prepforge.repository.*; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/api/public")
public class PublicController {
 private final CompanyRepository c; private final QuestionRepository q; private final InterviewExperienceRepository e;
 public PublicController(CompanyRepository c,QuestionRepository q,InterviewExperienceRepository e){this.c=c;this.q=q;this.e=e;}
 @GetMapping("/stats") public Map<String,Object> stats(){return Map.of("companies",c.count(),"questions",q.count(),"experiences",e.count(),"years","10-year data architecture");}
}
