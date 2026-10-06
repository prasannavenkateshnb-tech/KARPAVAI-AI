package com.prepforge.service;

import com.prepforge.dto.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class KarpavaiAIService {
    private final ChatClient chatClient;

    public KarpavaiAIService(ObjectProvider<ChatClient.Builder> builderProvider) {
        ChatClient.Builder builder = builderProvider.getIfAvailable();
        this.chatClient = builder == null ? null : builder.build();
    }

    public boolean isConfigured() {
        return chatClient != null;
    }

    public AIAnswerEvaluationResponse evaluateAnswer(String question, String answer, String expectedAnswer) {
        String reference = expectedAnswer == null ? "" : expectedAnswer.trim();
        String studentAnswer = answer == null ? "" : answer.trim();

        String prompt = """
        Evaluate a student's technical interview answer fairly and evidence-first.

        QUESTION:
        %s

        REFERENCE ANSWER (optional):
        %s

        STUDENT ANSWER:
        %s

        EVALUATION RULES — FOLLOW THESE BEFORE WRITING ANY FEEDBACK:
        1. Determine the technically correct answer to the question first.
        2. Evaluate ONLY what the student actually wrote. Never attribute a statement
           to the student unless it is present in the student answer.
        3. Give full credit for factually correct statements even when grammar,
           spelling, capitalization, or wording is imperfect.
        4. A correct but brief definition is still correct. Do not force extra details
           into the answer merely to lower the score.
        5. Commonly accepted technical terminology is valid when used correctly.
           Examples: "blueprint/template" for a class, "instance" for an object,
           "dynamic memory allocation" for runtime object allocation, and
           "function calls itself" for recursion.
        6. A weakness MUST describe a real problem in the student's actual answer.
           If no real weakness exists, return an empty weaknesses array.
        7. A missing concept MUST be both relevant to the question and genuinely absent.
           Do not list unrelated or optional details as missing concepts.
        8. Never contradict the student's answer. For example, if the student says
           "dynamic memory allocation", do NOT say the student claimed "static memory
           allocation".
        9. If one phrase is misleading but the main definition is correct, identify
           that specific phrase and do not mark the whole answer incorrect.
        10. Score correctness first, then completeness and clarity.

        SCORING:
        9-10 = Correct and sufficiently complete.
        7-8 = Mostly correct with minor omissions or wording issues.
        5-6 = Partially correct with an important omission or significant error.
        3-4 = Significant misunderstanding, but some relevant knowledge is present.
        0-2 = Fundamentally incorrect or unrelated.

        Return ONLY JSON matching the AIAnswerEvaluationResponse structure.
        Do not use markdown or code fences.
        """.formatted(question == null ? "" : question, reference, studentAnswer);

        AIAnswerEvaluationResponse draft = call(prompt, AIAnswerEvaluationResponse.class);

        // A second, independent audit is used specifically for answer evaluation.
        // This prevents the local model from inventing weaknesses or reversing a
        // fact that the student explicitly stated.
        String auditPrompt = """
        Audit and correct the draft evaluation below. You are not evaluating the
        student's answer from scratch; you are checking whether the draft is
        evidence-grounded.

        QUESTION:
        %s

        REFERENCE ANSWER (optional):
        %s

        STUDENT ANSWER:
        %s

        DRAFT EVALUATION:
        %s

        AUDIT RULES:
        - Remove any weakness that is not directly supported by the student's answer.
        - Remove any missing concept that is unnecessary or is already present in the answer.
        - Never claim the student said something they did not say.
        - Preserve correct terminology such as blueprint/template, instance, dynamic
          memory allocation, recursion, etc.
        - If the student's core answer is correct, do not give a low score merely
          because it is brief.
        - If the draft contradicts an explicit student statement, correct the draft.
        - It is valid for weaknesses or missingConcepts to be empty arrays.
        - Keep useful suggestions, but do not turn optional details into errors.
        - Keep score from 0 to 10 as an integer.

        Return ONLY the corrected JSON object matching AIAnswerEvaluationResponse.
        No markdown, no code fences, and no explanation outside JSON.
        """.formatted(question == null ? "" : question, reference, studentAnswer, toJsonSafe(draft));

        AIAnswerEvaluationResponse audited = call(auditPrompt, AIAnswerEvaluationResponse.class);
        return normalizeEvaluation(audited);
    }

    private AIAnswerEvaluationResponse normalizeEvaluation(AIAnswerEvaluationResponse response) {
        if (response == null) {
            throw new IllegalStateException("AI returned an empty answer evaluation.");
        }
        int score = Math.max(0, Math.min(10, response.score()));
        return new AIAnswerEvaluationResponse(
                score,
                safeList(response.strengths()),
                safeList(response.weaknesses()),
                safeList(response.missingConcepts()),
                safeList(response.suggestions()),
                response.feedback() == null ? "" : response.feedback().trim()
        );
    }

    private java.util.List<String> safeList(java.util.List<String> values) {
        if (values == null) return java.util.List.of();
        return values.stream()
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .toList();
    }

    private String toJsonSafe(AIAnswerEvaluationResponse response) {
        return "{\"score\":" + response.score()
                + ",\"strengths\":" + quoteList(response.strengths())
                + ",\"weaknesses\":" + quoteList(response.weaknesses())
                + ",\"missingConcepts\":" + quoteList(response.missingConcepts())
                + ",\"suggestions\":" + quoteList(response.suggestions())
                + ",\"feedback\":\"" + escapeJson(response.feedback()) + "\"}";
    }

    private String quoteList(java.util.List<String> values) {
        if (values == null || values.isEmpty()) return "[]";
        return values.stream()
                .map(v -> "\"" + escapeJson(v) + "\"")
                .collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    public AIQuestionResponse generateQuestions(String company, String role, String skill, String difficulty, int count) {
        int safeCount = Math.max(1, Math.min(count, 15));
        String prompt = """
        Generate exactly %d technical interview questions for placement preparation.
        Company: %s
        Role: %s
        Skill: %s
        Difficulty: %s

        These are AI-generated practice questions, not claims about actual company questions.
        Include a short answer hint for every question.
        Return exactly %d question objects in the questions array.
        Return ONLY valid JSON. Do not use markdown or code fences. Do not add any extra fields.
        """.formatted(safeCount, company, role, skill, difficulty, safeCount);
        return call(prompt, AIQuestionResponse.class);
    }

    public AIStudyPlanResponse studyPlan(String company, String role, String performance, String weakAreas, int days) {
        int safeDays = Math.max(1, Math.min(days, 30));
        String prompt = """
        Create a practical placement preparation study plan for exactly %d days.
        Company: %s
        Target role: %s
        Current performance: %s
        Weak areas: %s

        Prioritize weak areas and include DSA, core CS, Java/Spring/SQL where relevant.
        Keep tasks realistic for a student. Do not claim knowledge of confidential company material.
        Return exactly %d day objects in the days array, numbered 1 through %d.
        Return ONLY valid JSON. Do not use markdown, comments, or code fences.
        """.formatted(safeDays, company, role, performance, weakAreas, safeDays, safeDays);
        return call(prompt, AIStudyPlanResponse.class);
    }

    public AIPerformanceResponse performance(String performance, String weakAreas, String solved, String mockTests) {
        String prompt = """
        Analyze this student's placement preparation data and provide constructive guidance.
        Performance data: %s
        Weak areas: %s
        Questions solved: %s
        Mock tests completed: %s
        Do not make a hiring prediction. Give study-focused feedback only.
        Return ONLY valid JSON matching the requested structure. Do not use markdown or code fences.
        """.formatted(performance, weakAreas, solved, mockTests);
        return call(prompt, AIPerformanceResponse.class);
    }

    public AIMockInterviewResponse mockInterview(String company, String role, String difficulty, String interviewType,
                                                   String previousQuestion, String previousAnswer) {
        String prompt = """
        Act as a technical mock interviewer for a placement student.
        Company: %s
        Role: %s
        Difficulty: %s
        Interview type: %s
        Previous question: %s
        Previous answer: %s

        If a previous answer exists, evaluate it briefly and then ask the next relevant question.
        If there is no previous answer, start with a suitable question.
        Do not claim the question was actually asked by the company.
        Return ONLY valid JSON matching the requested structure. Do not use markdown or code fences.
        """.formatted(company, role, difficulty, interviewType,
                previousQuestion == null ? "" : previousQuestion,
                previousAnswer == null ? "" : previousAnswer);
        return call(prompt, AIMockInterviewResponse.class);
    }

    public AICompanyPreparationResponse companyPreparation(String company, String role, String skillLevel, String performance) {
        String prompt = """
        Create a company-focused placement preparation guide.
        Company: %s
        Role: %s
        Student skill level: %s
        Previous performance: %s

        Give important preparation topics, general interview areas, DSA topics, technical topics
        and practical recommendations. Do not present AI-generated content as actual company
        interview questions or confidential company material.
        Return ONLY valid JSON matching the requested structure. Do not use markdown or code fences.
        """.formatted(company, role, skillLevel, performance);
        return call(prompt, AICompanyPreparationResponse.class);
    }

    private <T> T call(String prompt, Class<T> type) {
        if (chatClient == null) {
            throw new IllegalStateException("Spring AI ChatClient is not configured.");
        }

        try {
            return request(prompt, type);
        } catch (RuntimeException firstFailure) {
            // Llama models can occasionally drift from the requested object shape.
            // Ollama JSON mode is enabled globally in application.properties; this second
            // attempt gives the model an even stricter instruction without changing the UI.
            String retryPrompt = prompt + """

        FINAL OUTPUT RULES:
        1. Output one JSON object only.
        2. Use double quotes around every property name and string value.
        3. Use a colon after every property name.
        4. Do not include trailing commas.
        5. Do not include ```json or ``` markers.
        6. Do not write any text before or after the JSON object.
        """;
            return request(retryPrompt, type);
        }
    }

    private <T> T request(String prompt, Class<T> type) {
        return chatClient.prompt()
                .system("""
                        You are KARPAVAI.AI AI, a placement preparation assistant.
                        The application requires machine-readable structured output.
                        Follow the target Java structure exactly.
                        Return only valid RFC 8259 JSON with no markdown or extra text.
                        """)
                .user(prompt)
                .call()
                .entity(type);
    }
}
