package it.unical.demacs.informatica.unicareermanager.costants;

import it.unical.demacs.informatica.unicareermanager.model.ThemeModel;

import java.util.Currency;
import java.util.Locale;

public interface Settings {
    // dimensions
    int LOGIN_VIEW_WIDTH = 800;
    int LOGIN_VIEW_HEIGHT = 650;
    int MAIN_VIEW_WIDTH = 1250;
    int MAIN_VIEW_HEIGHT = 700;
    int MAIN_VIEW_SIDEBAR_SIZE = 175;
    int MAIN_VIEW_STRETCHED_SIZE = 75;
    int ADD_EDIT_WIDTH = 400;
    int ADD_EDIT_HEIGHT = 450;
    // graphic chart setting
    double MAX_DERIVE_PIE_CHART = 50.0;
    // default settings
    double DEFAULT_MAX_CREDITS = 180.0;
    int DEFAULT_GRADE_VALUE = 30;
    String DEFAULT_CURRENCY = Currency.getInstance(Locale.getDefault()).getSymbol(Locale.US);
    ThemeModel DEFAULT_THEME = ThemeModel.LIGHT;
    // agenda calendars name
    String[] CALENDARS_NAME = {"Opzionale", "Normale", "Importante", "Urgente"};
    // maximum degree grade
    int MAX_DEGREE_GRADE = 110;
    // maximum grade
    int MAX_GRADE = 30;
    // minimum grade
    int MIN_GRADE = 18;
}
