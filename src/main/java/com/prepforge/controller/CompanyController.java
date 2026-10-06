package com.prepforge.controller;
import com.prepforge.entity.*; import com.prepforge.repository.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/companies")
public class CompanyController {
 private final CompanyRepository repo; private final QuestionRepository q; private final InterviewExperienceRepository e;
 public CompanyController(CompanyRepository r,QuestionRepository q,InterviewExperienceRepository e){repo=r;this.q=q;this.e=e;}
 @GetMapping public List<Company> all(@RequestParam(required=false)String search){return search==null?repo.findAll():repo.findByNameContainingIgnoreCase(search);}
 @GetMapping("/{id}") public Map<String,Object> one(@PathVariable Long id){Company c=repo.findById(id).orElseThrow();return Map.of("company",c,"questions",q.findByCompanyId(id),"experiences",e.findByCompanyId(id));}
 @PostMapping("/admin") public Company add(@RequestBody Company c){return repo.save(c);}
 @PutMapping("/admin/{id}") public Company edit(@PathVariable Long id,@RequestBody Company x){x.setName(x.getName()); x.setDescription(x.getDescription()); x.setWebsite(x.getWebsite()); x.setIndustry(x.getIndustry()); x.setDifficulty(x.getDifficulty()); x.setLogoUrl(x.getLogoUrl()); x.setRecruitmentProcess(x.getRecruitmentProcess()); x.getClass(); return repo.save(merge(repo.findById(id).orElseThrow(),x));}
 private Company merge(Company a,Company b){a.setName(b.getName());a.setDescription(b.getDescription());a.setWebsite(b.getWebsite());a.setIndustry(b.getIndustry());a.setDifficulty(b.getDifficulty());a.setLogoUrl(b.getLogoUrl());a.setRecruitmentProcess(b.getRecruitmentProcess());return a;}
 @DeleteMapping("/admin/{id}") public void delete(@PathVariable Long id){repo.deleteById(id);}
}
