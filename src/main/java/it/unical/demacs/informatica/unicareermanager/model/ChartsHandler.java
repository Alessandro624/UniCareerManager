package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.util.DateUtils;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import javafx.concurrent.Task;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChartsHandler {
    private static final ChartsHandler instance = new ChartsHandler();

    private ChartsHandler() {

    }

    public static ChartsHandler getInstance() {
        return instance;
    }

    public Task<Map<String, Double>> getAverageGradesOverTime(boolean avg, List<Exam> exams) {
        // a task to get avg over time
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Objects.requireNonNull(exams, "Exams cannot be null");
            List<Exam> sortedExams = exams.stream()
                    .sorted(Comparator.comparing(e -> DateUtils.parseDate(e.date())))
                    .toList();
            // LinkedHashMap -> follows insertion order
            Map<String, Double> avgData = new LinkedHashMap<>();
            double totalGrades = 0;
            int examCount = 0;
            for (Exam e : sortedExams) {
                if (e.getGradeAsInteger() > 0) {
                    if (avg) {
                        totalGrades += e.getGradeAsInteger();
                        examCount++;
                    } else {
                        totalGrades += e.getGradeAsInteger() * e.credits();
                        examCount += e.credits();
                    }
                    double avgGrade = totalGrades / examCount;
                    avgData.put(e.date(), avgGrade);
                }
            }
            return avgData;
        });
    }

    public Task<Map<String, Integer>> getGradeCount(List<Exam> exams) {
        // a task to get grade count
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Objects.requireNonNull(exams, "Exams cannot be null");
            Map<String, Integer> gradeCountMap = new HashMap<>();
            for (Exam e : exams) {
                String grade = e.grade();
                gradeCountMap.put(grade, gradeCountMap.getOrDefault(grade, 0) + 1);
            }
            return gradeCountMap;
        });
    }
}
