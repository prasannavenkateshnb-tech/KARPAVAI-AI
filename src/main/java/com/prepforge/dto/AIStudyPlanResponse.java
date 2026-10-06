package com.prepforge.dto;
import java.util.List;
public record AIStudyPlanResponse(List<StudyDay> days, String overallAdvice) {
    public record StudyDay(int day, List<StudyTask> tasks) {}
    public record StudyTask(String topic, String priority, String duration, String practice, String revision) {}
}