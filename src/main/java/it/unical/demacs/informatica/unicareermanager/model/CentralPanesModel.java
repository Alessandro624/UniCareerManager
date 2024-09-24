package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.costants.HelpMessages;

import java.util.HashMap;
import java.util.Map;

public class CentralPanesModel {
    public enum PaneType {
        HOME,
        STUDENT_CARD,
        CHARTS,
        PREVISION,
        SUBJECT,
        AGENDA,
        TAX,
        SETTINGS,
        ACCOUNT
    }

    private static final Map<PaneType, String> paneMap = new HashMap<>();

    static {
        paneMap.put(PaneType.HOME, "home-view.fxml");
        paneMap.put(PaneType.STUDENT_CARD, "student-card-view.fxml");
        paneMap.put(PaneType.CHARTS, "chart-view.fxml");
        paneMap.put(PaneType.PREVISION, "prevision-view.fxml");
        paneMap.put(PaneType.SUBJECT, "subject-view.fxml");
        paneMap.put(PaneType.AGENDA, "agenda-view.fxml");
        paneMap.put(PaneType.TAX, "tax-view.fxml");
        paneMap.put(PaneType.SETTINGS, "settings-view.fxml");
        paneMap.put(PaneType.ACCOUNT, "account-view.fxml");
    }

    private static final Map<PaneType, String> helpMap = new HashMap<>();

    static {
        helpMap.put(PaneType.HOME, HelpMessages.HOME_HELP);
        helpMap.put(PaneType.STUDENT_CARD, HelpMessages.STUDENT_CARD_HELP);
        helpMap.put(PaneType.CHARTS, HelpMessages.CHARTS_HELP);
        helpMap.put(PaneType.PREVISION, HelpMessages.PREVISION_HELP);
        helpMap.put(PaneType.SUBJECT, HelpMessages.SUBJECT_HELP);
        helpMap.put(PaneType.AGENDA, HelpMessages.AGENDA_HELP);
        helpMap.put(PaneType.TAX, HelpMessages.TAX_HELP);
        helpMap.put(PaneType.SETTINGS, HelpMessages.SETTINGS_HELP);
        helpMap.put(PaneType.ACCOUNT, HelpMessages.ACCOUNT_HELP);
    }

    public static String getPaneFileName(PaneType paneType) {
        return paneMap.get(paneType);
    }

    public static String getPaneHelp(PaneType paneType) {
        return helpMap.get(paneType);
    }
}
