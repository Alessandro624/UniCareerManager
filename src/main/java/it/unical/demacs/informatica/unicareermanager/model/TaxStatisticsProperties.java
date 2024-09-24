package it.unical.demacs.informatica.unicareermanager.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class TaxStatisticsProperties {
    private static TaxStatisticsProperties instance;

    private DoubleProperty totalPaid;
    private DoubleProperty totalToPay;

    private TaxStatisticsProperties() {
    }

    public static TaxStatisticsProperties getInstance() {
        if (instance == null) {
            instance = new TaxStatisticsProperties();
        }
        return instance;
    }

    public final DoubleProperty totalPaidProperty() {
        if (totalPaid == null)
            totalPaid = new SimpleDoubleProperty(0);
        return totalPaid;
    }

    public final DoubleProperty totalToPayProperty() {
        if (totalToPay == null)
            totalToPay = new SimpleDoubleProperty(0);
        return totalToPay;
    }

    public final double getTotalPaid() {
        return totalPaidProperty().get();
    }

    public final void setTotalPaid(double totalPaid) {
        totalPaidProperty().set(totalPaid);
    }

    public final double getTotalToPay() {
        return totalToPayProperty().get();
    }

    public final void setTotalToPay(double totalToPay) {
        totalToPayProperty().set(totalToPay);
    }
}
