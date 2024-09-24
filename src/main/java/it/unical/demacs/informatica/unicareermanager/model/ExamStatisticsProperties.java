package it.unical.demacs.informatica.unicareermanager.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ExamStatisticsProperties {
    private static ExamStatisticsProperties instance;

    private DoubleProperty avg;
    private DoubleProperty pAvg;
    private DoubleProperty totalCredits;

    private ExamStatisticsProperties() {
    }

    public static ExamStatisticsProperties getInstance() {
        if (instance == null) {
            instance = new ExamStatisticsProperties();
        }
        return instance;
    }

    public final DoubleProperty avgProperty() {
        if (avg == null)
            avg = new SimpleDoubleProperty(0);
        return avg;
    }

    public final DoubleProperty pAvgProperty() {
        if (pAvg == null)
            pAvg = new SimpleDoubleProperty(0);
        return pAvg;
    }

    public final DoubleProperty totalCreditsProperty() {
        if (totalCredits == null)
            totalCredits = new SimpleDoubleProperty(0);
        return totalCredits;
    }

    public final double getAvg() {
        return avgProperty().get();
    }

    public final void setAvg(double avg) {
        avgProperty().set(avg);
    }

    public final double getPAvg() {
        return pAvgProperty().get();
    }

    public final void setPAvg(double pAvg) {
        pAvgProperty().set(pAvg);
    }

    public final double getTotalCredits() {
        return totalCreditsProperty().get();
    }

    public final void setTotalCredits(double totalCredits) {
        totalCreditsProperty().set(totalCredits);
    }
}
