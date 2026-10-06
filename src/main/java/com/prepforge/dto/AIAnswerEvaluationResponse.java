package com.prepforge.dto;
import java.util.List;
public record AIAnswerEvaluationResponse(
    int score,
    List<String> strengths,
    List<String> weaknesses,
    List<String> missingConcepts,
    List<String> suggestions,
    String feedback
) {}