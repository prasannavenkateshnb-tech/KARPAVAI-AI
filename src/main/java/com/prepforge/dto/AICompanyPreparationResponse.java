package com.prepforge.dto;
import java.util.List;
public record AICompanyPreparationResponse(
    List<String> importantTopics,
    List<String> interviewAreas,
    List<String> dsaTopics,
    List<String> technicalTopics,
    List<String> recommendations
) {}