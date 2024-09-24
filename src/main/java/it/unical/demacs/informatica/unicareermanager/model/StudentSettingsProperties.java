package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.costants.Settings;
import javafx.beans.property.*;

public class StudentSettingsProperties {
    private static StudentSettingsProperties instance;

    private DoubleProperty maxCredits;
    private IntegerProperty gradeValue;
    private StringProperty currency;
    private ObjectProperty<ThemeModel> theme;

    private StudentSettingsProperties() {

    }

    public static StudentSettingsProperties getInstance() {
        if (instance == null)
            instance = new StudentSettingsProperties();
        return instance;
    }

    public final DoubleProperty maxCreditsProperty() {
        if (maxCredits == null)
            maxCredits = new SimpleDoubleProperty(Settings.DEFAULT_MAX_CREDITS);
        return maxCredits;
    }

    public final IntegerProperty gradeValueProperty() {
        if (gradeValue == null)
            gradeValue = new SimpleIntegerProperty(Settings.DEFAULT_GRADE_VALUE);
        return gradeValue;
    }

    public final StringProperty currencyProperty() {
        if (currency == null)
            currency = new SimpleStringProperty(Settings.DEFAULT_CURRENCY);
        return currency;
    }

    public final ObjectProperty<ThemeModel> themeProperty() {
        if (theme == null)
            theme = new SimpleObjectProperty<>(Settings.DEFAULT_THEME);
        return theme;
    }

    public final double getMaxCredits() {
        return maxCreditsProperty().get();
    }

    public final void setMaxCredits(double maxCredits) {
        maxCreditsProperty().set(maxCredits);
    }

    public final int getGradeValue() {
        return gradeValueProperty().get();
    }

    public final void setGradeValue(int gradeValue) {
        gradeValueProperty().set(gradeValue);
    }

    public final String getCurrency() {
        return currencyProperty().get();
    }

    public final void setCurrency(String currency) {
        currencyProperty().set(currency);
    }

    public final ThemeModel getTheme() {
        return themeProperty().get();
    }

    public final void setTheme(ThemeModel theme) {
        themeProperty().set(theme);
    }
}
