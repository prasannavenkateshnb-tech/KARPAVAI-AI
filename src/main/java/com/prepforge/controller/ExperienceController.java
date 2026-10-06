package com.prepforge.controller;
import com.prepforge.entity.InterviewExperience; import com.prepforge.repository.InterviewExperienceRepository; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/interview-experiences")
public class ExperienceController {
 private final InterviewExperienceRepository repo; public ExperienceController(InterviewExperienceRepository r){repo=r;}
 @GetMapping public List<InterviewExperience> all(){return repo.findAll();}
 @GetMapping("/{id}") public InterviewExperience one(@PathVariable Long id){return repo.findById(id).orElseThrow();}
 @PostMapping("/admin") public InterviewExperience add(@RequestBody InterviewExperience x){return repo.save(x);}
 @PutMapping("/admin/{id}") public InterviewExperience edit(@PathVariable Long id,@RequestBody InterviewExperience x){InterviewExperience a=repo.findById(id).orElseThrow();a.setCompany(x.getCompany());a.setYear(x.getYear());a.setRole(x.getRole());a.setRounds(x.getRounds());a.setDifficulty(x.getDifficulty());a.setInterviewType(x.getInterviewType());a.setSelectionResult(x.getSelectionResult());a.setDescription(x.getDescription());a.setQuestionsAsked(x.getQuestionsAsked());a.setPreparationTips(x.getPreparationTips());return repo.save(a);}
 @DeleteMapping("/admin/{id}") public void delete(@PathVariable Long id){repo.deleteById(id);}
}
