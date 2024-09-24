package it.unical.demacs.informatica.unicareermanager.costants;

public interface Message {
    // successful messages
    String SUCCESSFUL_SIGNUP = "Registrazione completata con successo";
    String SUCCESSFUL_LOGIN = "Accesso effettuato con successo";
    String ADDED_TAX = "Tassa aggiunta con successo";
    String EDITED_TAX = "Tassa modificata con successo";
    String ADDED_EXAM = "Esame aggiunto con successo";
    String EDITED_EXAM = "Esame modificato con successo";
    String ADDED_SUBJECT = "Materia aggiunta con successo";
    String EDITED_SUBJECT = "Materia modificata con successo";
    String EDITED_USER = "Profilo utente modificato con successo";
    String EDITED_SETTINGS = "Impostazioni modificate con successo";
    String RESTORED_SETTINGS = "Impostazioni predefinite ripristinate con successo";
    String SUCCESSFUL_LOGOUT = "Uscita dall'account avvenuta con successo";
    // user error messages
    String WRONG_MAT_UNI_PASS = "Matricola, università o password errata!";
    String PASSWORD_TOO_SHORT = "La password deve contenere almeno 8 caratteri!";
    String PASSWORD_CHARACTERS_ERROR = "La password deve contenere almeno un carattere minuscolo e uno maiuscolo!";
    String PASSWORD_DIGITS_ERROR = "La password deve contenere almeno un numero!";
    String ALREADY_USED_MAT_UNI = "Coppia matricola e università già utilizzata!";
    String ALL_FIELDS_ARE_MANDATORY = "Tutti i campi sono obbligatori!";
    String EDIT_USER_NOT_EMPTY_FIELD = "I campi nome e cognome non possono essere vuoti!";
    String ONLY_LETTERS_PROFESSOR_FIELD = "Il campo professore può contenere solo lettere!";
    String ONLY_LETTERS_UNIVERSITY_FIELD = "Il campo università può contenere solo lettere!";
    String ONLY_LETTERS_SIGNUP_FIELD = "I campi nome, cognome e università possono contenere solo lettere!";
    String ONLY_LETTERS_EDIT_USER_FIELD = "I campi nome, cognome, dipartimento e corso di studi possono contenere solo lettere!";
    String PAID_DATE_FIELDS_MANDATORY = "Il campo data pagamento è obbligatorio se la tassa è stata pagata!";
    String INVALID_FORMAT_AMOUNT = "Importo non valido, inserire un numero valido!";
    String AMOUNT_GREATER_THAN_ZERO = "Importo non valido, inserire un numero maggiore di 0!";
    String INVALID_FORMAT_CREDITS = "Crediti non validi, inserire un numero valido!";
    String CREDITS_GREATER_THAN_ZERO = "Crediti non validi, inserire un numero maggiore di 0!";
    String EMPTY_EVENTS = "Nessun evento inserito";
    String NOT_EDITED = "Nessun campo è stato modificato!";
    // confirmation messages
    String REMOVE_TAX_CONFIRMATION = "Vuoi davvero eliminare la tassa selezionata?";
    String EDIT_TAX_CONFIRMATION = "Vuoi davvero modificare la tassa selezionata?";
    String REMOVE_EXAM_CONFIRMATION = "Vuoi davvero eliminare l'esame selezionato?";
    String EDIT_EXAM_CONFIRMATION = "Vuoi davvero modificare l'esame selezionato?";
    String REMOVE_SUBJECT_CONFIRMATION = "Vuoi davvero eliminare la materia selezionato?";
    String EDIT_SUBJECT_CONFIRMATION = "Vuoi davvero modificare la materia selezionato?";
    String REMOVE_IMAGE_CONFIRMATION = "Vuoi davvero eliminare l'immagine profilo?";
    String EDIT_USER_CONFIRMATION = "Vuoi davvero modificare i tuoi dati?";
    String EDIT_SETTINGS_CONFIRMATION = "Vuoi davvero modificare le tue impostazioni?";
    String RESTORE_SETTINGS_CONFIRMATION = "Vuoi davvero ripristinare le impostazioni predefinite?";
    String LOGOUT_CONFIRMATION = """
            Vuoi davvero effettuare il logout?
            I dati non salvati andranno persi!
            """;
    String CLOSE_CONFIRMATION = """
            Vuoi davvero chiudere l'applicazione?
            I dati non salvati andranno persi!
            """;
    String DELETE_ALL_EVENT_CONFIRMATION = """
            Vuoi davvero eliminare tutti gli eventi?
            Gli eventi eliminati non potranno più essere recuperati!
            """;
    // application error messages
    String ON_START_ERROR = "Errore 100: caricamento dell'applicazione fallito";
    String LOGIN_ERROR = "Error 101: accesso fallito";
    String SIGNUP_ERROR = "Error 102: registrazione fallita";
    String LOGOUT_ERROR = "Error 103: uscita dall'account fallita";
    String LOAD_TAX_ERROR = "Error 104: caricamento delle tasse fallito";
    String ADD_TAX_ERROR = "Error 105: impossibile aggiungere tasse";
    String REMOVE_TAX_ERROR = "Error 106: impossibile rimuovere tasse";
    String EDIT_TAX_ERROR = "Error 107: impossibile modificare tasse";
    String LOAD_EXAM_ERROR = "Error 108: caricamento degli esami fallito";
    String ADD_EXAM_ERROR = "Error 109: impossibile aggiungere esami";
    String REMOVE_EXAM_ERROR = "Error 110: impossibile rimuovere esami";
    String EDIT_EXAM_ERROR = "Error 111: impossibile modificare esami";
    String LOAD_CHART_ERROR = "Error 112: caricamento dei grafici fallito";
    String LOAD_SUBJECT_ERROR = "Error 113: caricamento delle materie fallito";
    String ADD_SUBJECT_ERROR = "Error 114: impossibile aggiungere materie";
    String REMOVE_SUBJECT_ERROR = "Error 115: impossibile rimuovere materie";
    String EDIT_SUBJECT_ERROR = "Error 116: impossibile modificare materie";
    String LOAD_PREVISION_ERROR = "Error 117: impossibile calcolare previsioni";
    String LOAD_IMAGE_ERROR = "Error 118: caricamento dell'immagine profilo fallito";
    String EDIT_USER_ERROR = "Error 119: impossibile modificare profilo utente";
    String LOAD_SETTINGS_ERROR = "Error 120: caricamento delle impostazioni fallito";
    String EDIT_SETTINGS_ERROR = "Error 121: impossibile modificare impostazioni";
    String RESTORE_SETTINGS_ERROR = "Error 122: impossibile ripristinare impostazioni predefinite";
    String LOAD_ENTRY_ERROR = "Error 123: caricamento dell'agenda fallito";
    String EVENT_INSERT_ERROR = "Error 124: impossibile aggiungere evento";
    String EVENT_UPDATE_ERROR = "Error 125: impossibile modificare evento";
    String EVENT_DELETE_ERROR = "Error 126: impossibile eliminare evento";
    String UPDATE_ERROR = "Error 127: impossibile aggiornare statistiche studente";
}
