package com.prepforge.controller;
import com.prepforge.entity.*; import com.prepforge.repository.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/questions")
public class QuestionController {
 private final QuestionRepository repo; private final CompanyRepository companies; private final QuestionAttemptRepository attempts; private final UserRepository users;
 public QuestionController(QuestionRepository r,CompanyRepository c,QuestionAttemptRepository a,UserRepository u){repo=r;companies=c;attempts=a;users=u;}
 @GetMapping public List<Question> all(@RequestParam(required=false)String search,@RequestParam(required=false)Long companyId,@RequestParam(required=false)String category){
  if(search!=null&&!search.isBlank())return repo.findByQuestionTextContainingIgnoreCase(search);
  if(companyId!=null)return repo.findByCompanyId(companyId); if(category!=null)return repo.findByCategoryIgnoreCase(category); return repo.findAll();
 }
 @GetMapping("/{id}") public Question one(@PathVariable Long id){return repo.findById(id).orElseThrow();}
 @PostMapping("/admin") public Question add(@RequestBody Question q){return repo.save(q);}
 @PutMapping("/admin/{id}") public Question edit(@PathVariable Long id,@RequestBody Question x){Question q=repo.findById(id).orElseThrow();q.setCompany(x.getCompany());q.setYear(x.getYear());q.setRole(x.getRole());q.setRound(x.getRound());q.setCategory(x.getCategory());q.setTopic(x.getTopic());q.setDifficulty(x.getDifficulty());q.setFrequency(x.getFrequency());q.setTags(x.getTags());q.setQuestionText(x.getQuestionText());q.setExpectedAnswer(x.getExpectedAnswer());q.setExplanation(x.getExplanation());return repo.save(q);}
 @DeleteMapping("/admin/{id}") public void delete(@PathVariable Long id){repo.deleteById(id);}
 @PostMapping("/{id}/solve") public Map<String,Object> solve(@PathVariable Long id,java.security.Principal p){
  User u=users.findByEmail(p.getName()).orElseThrow(); Question q=repo.findById(id).orElseThrow(); QuestionAttempt a=attempts.findByUserIdAndQuestionId(u.getId(),id).orElse(new QuestionAttempt(u,q,true)); a.setSolved(true);attempts.save(a);return Map.of("solvedCount",attempts.countByUserIdAndSolvedTrue(u.getId()));
 }
 @PostMapping("/{id}/bookmark") public Map<String,String> bookmark(@PathVariable Long id,java.security.Principal p){
  User u=users.findByEmail(p.getName()).orElseThrow(); QuestionAttempt a=attempts.findByUserIdAndQuestionId(u.getId(),id).orElse(new QuestionAttempt(u,repo.findById(id).orElseThrow(),false));a.setBookmarked(!a.isBookmarked());attempts.save(a);return Map.of("message",a.isBookmarked()?"Bookmarked":"Removed");
 }
 @GetMapping("/admin/all") public List<Question> adminAll(){return repo.findAll();}
}
