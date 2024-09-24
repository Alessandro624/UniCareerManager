package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

public class TaxStatisticsService extends Service<TaxStatistics> {
    private List<Tax> paidTaxes;
    private List<Tax> toPayTaxes;

    public void setPaidTaxes(List<Tax> paidTaxes) {
        this.paidTaxes = paidTaxes;
    }

    public void setToPayTaxes(List<Tax> toPayTaxes) {
        this.toPayTaxes = toPayTaxes;
    }

    @Override
    protected Task<TaxStatistics> createTask() {
        // a task to update tax statistics
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            double totalPaid = paidTaxes.stream()
                    .mapToDouble(Tax::amount)
                    .sum();
            double totalToPay = toPayTaxes.stream()
                    .mapToDouble(Tax::amount)
                    .sum();
            return new TaxStatistics(totalPaid, totalToPay);
        });
    }
}
