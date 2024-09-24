package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.costants.Settings;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class ExamPrevisionService extends Service<List<ExamPrevision>> {
    private List<Exam> exams;
    private double previsionCredits;

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    public void setPrevisionCredits(double previsionCredits) {
        this.previsionCredits = previsionCredits;
    }

    @Override
    protected Task<List<ExamPrevision>> createTask() {
        // a task to compute previsions
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            List<ExamPrevision> previsions = new ArrayList<>();
            int totalGrade = exams.stream()
                    .mapToInt(Exam::getGradeAsInteger)
                    .sum();
            long countExams = exams.stream()
                    .filter(exam -> exam.getGradeAsInteger() > 0)
                    .count() + 1;
            double totalCredits = exams.stream().
                    filter(exam -> exam.getGradeAsInteger() > 0)
                    .mapToDouble(Exam::credits)
                    .sum() + previsionCredits;
            double weightedTotalGrade = exams.stream()
                    .filter(exam -> exam.getGradeAsInteger() > 0)
                    .mapToDouble(exam -> exam.getGradeAsInteger() * exam.credits())
                    .sum();
            for (int i = Settings.MIN_GRADE; i <= Settings.MAX_GRADE; i++) {
                double newAvg = (double) (totalGrade + i) / countExams;
                double newPAvg = (weightedTotalGrade + (i * previsionCredits)) / totalCredits;
                previsions.add(new ExamPrevision(String.valueOf(i), newAvg, newPAvg));
            }
            double newAvg = (double) (totalGrade + StudentSettingsProperties.getInstance().getGradeValue()) / countExams;
            double newPAvg = (weightedTotalGrade + (StudentSettingsProperties.getInstance().getGradeValue() * previsionCredits)) / totalCredits;
            previsions.add(new ExamPrevision("30 e lode", newAvg, newPAvg));
            return previsions;
        });
    }
}
