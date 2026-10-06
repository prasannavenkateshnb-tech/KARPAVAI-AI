package com.prepforge.dto;
import java.util.List;
public record AIMockInterviewResponse(
    String question,
    String score,
    List<String> strengths,
    List<String> missingConcepts,
    String feedback,
    String nextQuestion
) {}