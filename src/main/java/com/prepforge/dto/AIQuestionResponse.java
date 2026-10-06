package com.prepforge.dto;
import java.util.List;
public record AIQuestionResponse(List<GeneratedQuestion> questions) {
    public record GeneratedQuestion(String question, String category, String topic, String difficulty, String answerHint) {}
}