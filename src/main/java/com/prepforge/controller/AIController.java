package com.prepforge.controller;

import com.prepforge.dto.*;
import com.prepforge.service.KarpavaiAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private static final Logger log = LoggerFactory.getLogger(AIController.class);
    private final KarpavaiAIService ai;

    public AIController(KarpavaiAIService ai) { this.ai = ai; }

    @GetMapping("/status")
    public Map<String,Object> status() {
        return Map.of("configured", ai.isConfigured(), "provider", "Ollama via Spring AI");
    }

    @PostMapping("/evaluate-answer")
    public ResponseEntity<?> evaluateAnswer(@RequestBody AnswerRequest r) {
        try {
            return ResponseEntity.ok(ai.evaluateAnswer(r.question(), r.answer(), r.expectedAnswer()));
        } catch (Exception e) {
            log.error("AI answer evaluation failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "AI service is unavailable. Please try again later."));
        }
    }

    @PostMapping("/mock-interview")
    public ResponseEntity<?> mockInterview(@RequestBody MockInterviewRequest r) {
        try { return ResponseEntity.ok(ai.mockInterview(r.company(), r.role(), r.difficulty(), r.interviewType(), r.previousQuestion(), r.previousAnswer())); }
        catch (Exception e) { log.error("AI mock interview failed", e); return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "AI interview is temporarily unavailable.")); }
    }

    @PostMapping("/questions")
    public ResponseEntity<?> questions(@RequestBody QuestionRequest r) {
        try { return ResponseEntity.ok(ai.generateQuestions(r.company(), r.role(), r.skill(), r.difficulty(), r.count())); }
        catch (Exception e) { log.error("AI question generation failed", e); return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "AI question generation is temporarily unavailable.")); }
    }

    @PostMapping("/study-plan")
    public ResponseEntity<?> studyPlan(@RequestBody StudyPlanRequest r) {
        try { return ResponseEntity.ok(ai.studyPlan(r.company(), r.role(), r.performance(), r.weakAreas(), r.days())); }
        catch (Exception e) { log.error("AI study plan generation failed", e); return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "AI study plan generation is temporarily unavailable.")); }
    }

    @PostMapping("/performance")
    public ResponseEntity<?> performance(@RequestBody PerformanceRequest r) {
        try { return ResponseEntity.ok(ai.performance(r.performance(), r.weakAreas(), r.solved(), r.mockTests())); }
        catch (Exception e) { log.error("AI performance analysis failed", e); return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "AI performance analysis is temporarily unavailable.")); }
    }

    @PostMapping("/company-preparation")
    public ResponseEntity<?> companyPreparation(@RequestBody CompanyPreparationRequest r) {
        try { return ResponseEntity.ok(ai.companyPreparation(r.company(), r.role(), r.skillLevel(), r.performance())); }
        catch (Exception e) { log.error("AI company preparation failed", e); return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "AI company preparation is temporarily unavailable.")); }
    }

    public record AnswerRequest(String question, String answer, String expectedAnswer) {}
    public record MockInterviewRequest(String company, String role, String difficulty, String interviewType, String previousQuestion, String previousAnswer) {}
    public record QuestionRequest(String company, String role, String skill, String difficulty, int count) {}
    public record StudyPlanRequest(String company, String role, String performance, String weakAreas, int days) {}
    public record PerformanceRequest(String performance, String weakAreas, String solved, String mockTests) {}
    public record CompanyPreparationRequest(String company, String role, String skillLevel, String performance) {}
}
