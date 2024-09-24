package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

public class ExamStatisticsService extends Service<ExamStatistics> {
    private List<Exam> exams;

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    @Override
    protected Task<ExamStatistics> createTask() {
        // a task to compute student statistics
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            int totalGrade = exams.stream()
                    .mapToInt(Exam::getGradeAsInteger)
                    .sum();
            long countExams = exams.stream()
                    .filter(exam -> exam.getGradeAsInteger() > 0)
                    .count();
            double totalCredits = exams.stream()
                    .mapToDouble(Exam::credits)
                    .sum();
            double zeroGradeCredits = exams.stream()
                    .filter(exam -> exam.getGradeAsInteger() == 0)
                    .mapToDouble(Exam::credits)
                    .sum();
            double weightedTotalGrade = exams.stream()
                    .filter(exam -> exam.getGradeAsInteger() > 0)
                    .mapToDouble(exam -> exam.getGradeAsInteger() * exam.credits())
                    .sum();
            double creditsPAvg = totalCredits - zeroGradeCredits;
            double avg = countExams != 0 ? (double) totalGrade / countExams : 0;
            double pAvg = creditsPAvg != 0 ? weightedTotalGrade / creditsPAvg : 0;
            return new ExamStatistics(totalCredits, avg, pAvg);
        });
    }
}
