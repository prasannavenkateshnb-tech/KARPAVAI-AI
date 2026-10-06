package com.prepforge.dto;
import java.util.List;
public record AIPerformanceResponse(
    List<String> strongAreas,
    List<String> weakAreas,
    List<String> revisionTopics,
    String readinessFeedback,
    List<String> recommendedNextTopics
) {}