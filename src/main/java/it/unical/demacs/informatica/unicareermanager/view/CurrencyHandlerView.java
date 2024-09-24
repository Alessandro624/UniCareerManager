package it.unical.demacs.informatica.unicareermanager.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;

public class CurrencyHandlerView {
    private final ObservableList<String> currencies = FXCollections.observableArrayList();

    private final static CurrencyHandlerView instance = new CurrencyHandlerView();

    private CurrencyHandlerView() {
        for (Currency currency : Currency.getAvailableCurrencies())
            currencies.add(currency.getCurrencyCode() + " " + currency.getSymbol(Locale.US));
        currencies.sort(Comparator.comparing(currency -> currency.split(" +")[1].length()));
    }

    public static CurrencyHandlerView getInstance() {
        return instance;
    }

    public ObservableList<String> getCurrencies() {
        return FXCollections.unmodifiableObservableList(currencies);
    }
}
