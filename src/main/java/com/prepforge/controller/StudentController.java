package com.prepforge.controller;

import com.prepforge.entity.*;
import com.prepforge.repository.*;
import com.prepforge.service.KarpavaiAIService;
import com.prepforge.dto.AIAnswerEvaluationResponse;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final UserRepository users;
    private final QuestionAttemptRepository attempts;
    private final QuestionRepository questions;
    private final KarpavaiAIService ai;

    public StudentController(UserRepository u, QuestionAttemptRepository a, QuestionRepository q, KarpavaiAIService ai) {
        users = u;
        attempts = a;
        questions = q;
        this.ai = ai;
    }

    @GetMapping("/dashboard")
    public Map<String,Object> dashboard(Principal p) {
        User u = users.findByEmail(p.getName()).orElseThrow();
        long solved = attempts.countByUserIdAndSolvedTrue(u.getId());
        int readiness = Math.min(100, 35 + (int) solved * 3);
        return Map.of(
            "name", u.getName(),
            "solved", solved,
            "readiness", readiness,
            "mockTests", u.getMockTestsCompleted(),
            "experiences", u.getExperiencesRead(),
            "streak", u.getCurrentStreak()
        );
    }

    @GetMapping("/readiness")
    public Map<String,Object> readiness(Principal p) {
        User u = users.findByEmail(p.getName()).orElseThrow();
        long s = attempts.countByUserIdAndSolvedTrue(u.getId());
        int d = Math.min(100, 40 + (int)s * 4);
        int java = Math.min(100, 45 + (int)s * 3);
        int sql = Math.min(100, 35 + (int)s * 2);
        return Map.of(
            "overall", Math.min(100, (d + java + sql) / 3),
            "DSA", d,
            "Java", java,
            "SQL", sql,
            "weakAreas", sql < 50 ? List.of("SQL", "DBMS") : List.of("Consistency")
        );
    }

    @GetMapping("/bookmarks")
    public List<QuestionAttempt> bookmarks(Principal p) {
        User u = users.findByEmail(p.getName()).orElseThrow();
        return attempts.findByUserId(u.getId()).stream()
            .filter(QuestionAttempt::isBookmarked).toList();
    }

    /*
     * KARPAVAI.AI MOCK TEST
     * This is a local, explainable "AI-style" analysis engine.
     * It does not call an external AI API, so the project stays simple and
     * works completely offline once the application is running.
     */
    @GetMapping("/mock-test")
    public List<Map<String,Object>> mockTest() {
        List<Question> pool = new ArrayList<>(questions.findAll());
        Collections.shuffle(pool);
        int size = Math.min(10, pool.size());

        List<Map<String,Object>> result = new ArrayList<>();
        for (Question q : pool.subList(0, size)) {
            result.add(Map.of(
                "id", q.getId(),
                "questionText", q.getQuestionText(),
                "category", q.getCategory(),
                "topic", q.getTopic(),
                "difficulty", q.getDifficulty(),
                "company", q.getCompany().getName()
            ));
        }
        return result;
    }

    @PostMapping("/mock-test/submit")
    public Map<String,Object> submitMockTest(@RequestBody MockTestRequest request, Principal p) {
        User u = users.findByEmail(p.getName()).orElseThrow();
        List<Map<String,Object>> answers = request.answers == null ? List.of() : request.answers;

        int total = answers.size();
        int earned = 0;
        Map<String,Integer> categoryScores = new LinkedHashMap<>();
        Map<String,Integer> categoryTotals = new LinkedHashMap<>();

        List<String> focusAreas = new ArrayList<>();
        List<Map<String,Object>> aiFeedback = new ArrayList<>();

        for (Map<String,Object> item : answers) {
            Long questionId = toLong(item.get("questionId"));
            String answer = Objects.toString(item.get("answer"), "").trim();

            if (questionId == null) continue;
            Question q = questions.findById(questionId).orElse(null);
            if (q == null) continue;

            int score = analyzeAnswer(q, answer);
            earned += score;

            // Spring AI is an additional layer; the local scoring above remains the fallback.
            if (ai.isConfigured() && !answer.isBlank()) {
                try {
                    AIAnswerEvaluationResponse aiResult = ai.evaluateAnswer(
                        q.getQuestionText(), answer, q.getExpectedAnswer());
                    aiFeedback.add(Map.of(
                        "questionId", q.getId(),
                        "score", aiResult.score(),
                        "strengths", aiResult.strengths() == null ? List.of() : aiResult.strengths(),
                        "weaknesses", aiResult.weaknesses() == null ? List.of() : aiResult.weaknesses(),
                        "missingConcepts", aiResult.missingConcepts() == null ? List.of() : aiResult.missingConcepts(),
                        "suggestions", aiResult.suggestions() == null ? List.of() : aiResult.suggestions(),
                        "feedback", aiResult.feedback() == null ? "" : aiResult.feedback()
                    ));
                } catch (Exception ignored) {
                    // Keep the existing local analysis when the AI provider is unavailable.
                }
            }

            String category = q.getCategory() == null ? "General" : q.getCategory();
            categoryScores.merge(category, score, Integer::sum);
            categoryTotals.merge(category, 10, Integer::sum);
        }

        int maximum = Math.max(1, total * 10);
        int percentage = Math.round((earned * 100f) / maximum);

        for (String category : categoryTotals.keySet()) {
            int catPct = Math.round(
                categoryScores.getOrDefault(category, 0) * 100f /
                Math.max(1, categoryTotals.get(category))
            );
            if (catPct < 60) focusAreas.add(category);
        }

        if (focusAreas.isEmpty()) focusAreas.add("Consistency & timed practice");

        String level = percentage >= 80 ? "Interview Ready"
            : percentage >= 60 ? "Getting Stronger"
            : percentage >= 40 ? "Needs Focus"
            : "Foundation Building";

        String feedback;
        if (percentage >= 80) {
            feedback = "Strong performance. Keep your speed high and revise the topics where you lost marks.";
        } else if (percentage >= 60) {
            feedback = "Good base. Improve the highlighted focus areas and practice explaining answers clearly under time pressure.";
        } else {
            feedback = "Use this result as a study map. Build fundamentals first, then retake a mock test after targeted practice.";
        }

        u.incrementMocks();
        users.save(u);

        return Map.of(
            "score", earned,
            "total", maximum,
            "percentage", percentage,
            "level", level,
            "feedback", feedback,
            "focusAreas", focusAreas,
            "categoryScores", categoryScores,
            "aiAnalysisAvailable", ai.isConfigured(),
            "aiFeedback", aiFeedback
        );
    }

    private int analyzeAnswer(Question q, String answer) {
        if (answer.isBlank()) return 0;

        // Explainable local scoring: clarity + relevant concept words + useful length.
        int score = Math.min(4, Math.max(1, answer.length() / 80));
        String haystack = answer.toLowerCase(Locale.ROOT);
        String expected = Objects.toString(q.getExpectedAnswer(), "").toLowerCase(Locale.ROOT);

        String[] keywords = {
            "algorithm","complexity","time","space","array","hash","stack","queue",
            "object","class","inheritance","polymorphism","encapsulation","interface",
            "select","join","where","group","transaction","acid","process","thread",
            "http","api","database","normalization","index","scalability","behavior",
            "example","because","approach","trade-off"
        };

        int hits = 0;
        for (String keyword : keywords) {
            if (haystack.contains(keyword) && (expected.isBlank() || expected.contains(keyword) || q.getQuestionText().toLowerCase(Locale.ROOT).contains(keyword))) {
                hits++;
            }
        }

        score += Math.min(4, hits);
        if (answer.length() >= 140) score++;
        if (answer.length() >= 280) score++;
        return Math.min(10, score);
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        try { return Long.valueOf(String.valueOf(value)); }
        catch (Exception e) { return null; }
    }

    public static class MockTestRequest {
        public List<Map<String,Object>> answers;
    }
}
