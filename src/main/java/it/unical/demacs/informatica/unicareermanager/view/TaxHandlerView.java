package it.unical.demacs.informatica.unicareermanager.view;

import it.unical.demacs.informatica.unicareermanager.model.Tax;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class TaxHandlerView {
    private final ObservableList<Tax> paidTaxes = FXCollections.observableArrayList();
    private final ObservableList<Tax> toPayTaxes = FXCollections.observableArrayList();

    private final static TaxHandlerView instance = new TaxHandlerView();

    private final List<ListChangeListener<? super Tax>> changeListeners = new ArrayList<>();

    private boolean loaded = false;

    private TaxHandlerView() {
    }

    public static TaxHandlerView getInstance() {
        return instance;
    }

    public ObservableList<Tax> getPaidTaxes() {
        return FXCollections.unmodifiableObservableList(paidTaxes);
    }

    public ObservableList<Tax> getToPayTaxes() {
        return FXCollections.unmodifiableObservableList(toPayTaxes);
    }

    public void addTax(Tax tax, boolean isPaid) {
        if (isPaid) {
            addPaidTax(tax);
        } else {
            addToPayTax(tax);
        }
    }

    public void removeTax(Tax tax, boolean isPaid) {
        if (isPaid) {
            removePaidTax(tax);
        } else {
            removeToPayTax(tax);
        }
    }

    private void addToPayTax(Tax tax) {
        toPayTaxes.add(tax);
    }

    private void addPaidTax(Tax tax) {
        paidTaxes.add(tax);
    }

    private void removeToPayTax(Tax tax) {
        toPayTaxes.remove(tax);
    }

    private void removePaidTax(Tax tax) {
        paidTaxes.remove(tax);
    }

    public void addListener(ListChangeListener<? super Tax> taxListChangeListener) {
        changeListeners.add(taxListChangeListener);
        if (loaded) {
            toPayTaxes.addListener(taxListChangeListener);
            paidTaxes.addListener(taxListChangeListener);
        }
    }

    private void triggerChange() {
        // to trigger a change
        paidTaxes.setAll(new ArrayList<>(paidTaxes));
        toPayTaxes.setAll(new ArrayList<>(toPayTaxes));
    }

    public void loadCompleted() {
        if (!loaded) {
            changeListeners.forEach(toPayTaxes::addListener);
            changeListeners.forEach(paidTaxes::addListener);
            triggerChange();
            loaded = true;
        }
    }

    public void logout() {
        changeListeners.forEach(toPayTaxes::removeListener);
        changeListeners.forEach(paidTaxes::removeListener);
        changeListeners.clear();
        loaded = false;
        Platform.runLater(() -> {
            toPayTaxes.clear();
            paidTaxes.clear();
        });
    }
}
