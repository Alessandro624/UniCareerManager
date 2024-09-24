package it.unical.demacs.informatica.unicareermanager.costants;

public interface HelpMessages {
    String HEADER = "FAQs";
    String HOME_HELP = """
            D: Come posso navigare tra le diverse sezioni dell'applicazione?
            R: Utilizza il menu della barra laterale sinistra per navigare.
                Clicca su una qualsiasi delle icone come "Home", "Libretto", "Grafici",
                "Previsioni", "Materie", "Agenda", "Tasse", "Impostazioni" e "Studente"
                per accedere alle rispettive sezioni.

            D: Come posso cambiare il tema dell'applicazione?
            R: Fai clic sul pulsante "Cambia Tema" nell'angolo in alto a destra
                per passare dal tema chiaro a quello scuro (o viceversa).
            """;
    String STUDENT_CARD_HELP = """
            D: Come posso aggiungere un nuovo esame alla mia lista?
            R: Clicca sul pulsante "Aggiungi esame" nell'angolo in basso a destra
                per aggiungere un nuovo esame.

            D: Come posso trovare rapidamente un esame specifico?
            R: Utilizza la casella di testo "Filtro" in alto per cercare un esame per nome.
                Puoi anche utilizzare il menu a tendina per filtrare gli esami in base
                a diversi criteri.

            D: Come posso modificare un esame esistente?
            R: Fai clic sull'icona della matita (pulsante Modifica) accanto
                all'esame che desideri modificare.

            D: Come posso eliminare un esame dall'elenco?
            R: Fare clic sull'icona del cestino (pulsante Elimina) accanto all'esame
                che si desidera rimuovere.
            """;
    String CHARTS_HELP = """
            D: Come posso passare da un grafico all'altro?
            R: Utilizza le schede nella parte superiore dello schermo per passare
                da un grafico all'altro. Le schede disponibili includono
                "MEDIA", "MEDIA PONDERATA", "ESAMI PER VOTO",
                "CREDITI ACQUISITI" e "PERCENTUALE VOTI".

            D: Quali informazioni vengono visualizzate in ciascuna scheda?
            R:
                MEDIA: mostra la media dei voti nel tempo.
                MEDIA PONDERATA: mostra la media ponderata dei voti nel tempo.
                ESAMI PER VOTO: illustra la distribuzione dei voti nei diversi esami.
                CREDITI ACQUISITI: mostra la percentuale di crediti acquisiti.
                PERCENTUALE VOTI: visualizza la distribuzione in percentuale dei voti.
            """;
    String PREVISION_HELP = """
            D: Come inserisco i crediti per il mio prossimo esame?
            R: Inserisci il numero di crediti per il tuo prossimo esame nel campo
                di testo denominato "Crediti del prossimo esame" e poi clicca
                sul pulsante "Calcola" per aggiornare le tue previsioni.

            D: Cosa succede se non conosco i crediti per il mio prossimo esame?
            R: Se non conosci i crediti per il tuo prossimo esame, puoi controllare
            il programma del corso di studi o contattare il tuo insegnante.
            """;
    String SUBJECT_HELP = """
            D: Come posso trovare rapidamente una materia specifica?
            R: Utilizza la casella di testo "Filtro" in alto per cercare
                una materia per nome.
                Puoi anche utilizzare il menu a tendina per filtrare i corsi
                in base a diversi criteri.

            D: Come posso aggiungere una nuova materia?
            R: Clicca sul pulsante "Aggiungi materia" nella parte inferiore dello schermo.
                Inserisci i dettagli richiesti, quindi salva la nuova materia.

            D: Come posso modificare una materia esistente?
            R: Per modificare una materia, fai clic sull'icona della matita
                (pulsante Modifica) accanto al corso che desideri modificare.

            D: Come posso eliminare un corso?
            R: Per eliminare un corso, fai clic sull'icona del cestino (pulsante Elimina)
                accanto al corso che desideri rimuovere.
                Conferma l'eliminazione quando richiesto.
            """;
    String AGENDA_HELP = """
            D: Come posso visualizzare la mia agenda?
            R: La tua pianificazione viene visualizzata in formato calendario,
                mostrando una visualizzazione giornaliera o settimanale.
                Puoi passare da una visualizzazione all'altra utilizzando
                i pulsanti "Giorno" (Giorno) e "Settimana" (Settimana).

            D: Come posso vedere la data di oggi nella mia agenda?
            R: Fai clic sul pulsante "Oggi" per passare rapidamente alla data odierna.

            D: Come posso aggiungere un nuovo evento?
            R: Clicca e trascina sulla data e sulla fascia oraria desiderata
                nel calendario per creare un nuovo evento.
                In alternativa premi il tasto destro sul calendario e
                utilizza il menu contestuale.

            D: Come posso modificare un evento esistente?
            R: Fai doppio clic sull'evento che desideri modificare nel calendario.
                In alternativa premi il tasto destro sull'evento che desideri
                modificare e utilizza il menu contestuale.

            D: Come posso eliminare un evento?
            R: Premi il tasto destro sull'evento che desideri eliminare e
                cerca l'opzione "Cancella" nel menu contestuale.

            D: Come posso eliminare tutti gli eventi?
            R: Clicca sul pulsante "Elimina tutti gli eventi" per rimuovere
                tutti gli eventi dal tuo calendario.

            D: Come posso cercare un evento specifico?
            R: Utilizza la casella "Ricerca" nell'angolo in alto a destra dello schermo
                per trovare gli eventi per nome o parola chiave.
            """;
    String TAX_HELP = """
            D: Come posso trovare rapidamente una tassa specifica?
            R: Utilizza la casella "Filtro" per cercare tasse specifiche
                per nome o altri criteri.

            D: Come posso aggiungere una nuova tassa?
            R: Clicca sul pulsante "Aggiungi tassa" in basso a destra dello schermo.
                Compila i dati necessari e salva la nuova tassa.

            D: Come posso modificare una tariffa esistente?
            R: Fai clic sull'icona della matita (pulsante Modifica)
                accanto alla tariffa che desideri modificare.

            D: Come posso eliminare una tariffa?
            R: Fai clic sull'icona del cestino (pulsante Elimina) accanto
                alla tariffa che desideri eliminare.
                Conferma l'eliminazione quando richiesto.

            D: Cosa rappresentano le colonne nella tabella delle tariffe?

            D: Come posso vedere l'importo totale dovuto e pagato?
            R: L'importo totale dovuto e l'importo totale pagato vengono
                visualizzati nella parte inferiore dello schermo.
            """;
    String SETTINGS_HELP = """
            Q: Come posso modificare i crediti totali?
            A: Inserisci il nuovo valore nel campo "Crediti Totali" e conferma la modifica.

            Q: Perché dovrei modificare i crediti totali massimi?
            A: Modificare i crediti totali massimi può essere utile per adattare
                l'applicazione alle tue esigenze accademiche specifiche.

            Q: Come posso modificare il valore della lode?
            A: Inserisci il nuovo valore nel campo "Valore lode" e conferma la modifica.

            Q: Perché dovrei modificare il valore della lode?
            A: Il valore della lode può influenzare il calcolo della tua media.

            Q: Come posso cambiare il tema predefinito dell'applicazione?
            A: Seleziona il tema desiderato (chiaro o scuro) dall'opzione
                "Tema Predefinito" e conferma la modifica.

            Q: Perché cambiare il tema predefinito?
            A: Cambiare il tema predefinito può migliorare la tua esperienza visiva
                e rendere l'applicazione più piacevole da utilizzare.

            Q: Come posso cambiare la valuta?
            A: Seleziona la valuta desiderata (EUR, USD, ecc.) dal menu a tendina
                "Formato Valuta" e conferma la modifica.

            Q: Perché dovrei cambiare la valuta?
            A: Cambiare la valuta può essere utile se studi o lavori in un contesto internazionale
                e preferisci visualizzare i costi nella tua valuta locale.

            Q: Come faccio a confermare le modifiche nelle impostazioni?
            A: Dopo aver inserito i nuovi valori nei rispettivi campi, clicca sul pulsante
                "Salva modifiche" per confermare tutte le modifiche.

            Q: Posso ripristinare le impostazioni predefinite?
            A: Fai clic sul pulsante "Ripristina impostazioni predefinite" per
                riportare le tue impostazioni a quelle predefinite.
            """;
    String ACCOUNT_HELP = """
            Q: Come posso modificare il mio nome?
            A: Inserisci il nuovo nome nel campo "Nome" e
                conferma la modifica cliccando sul pulsante "Salva modifiche".

            Q: Come posso modificare il mio cognome?
            A: Inserisci il nuovo cognome nel campo "Cognome" e
                conferma la modifica cliccando sul pulsante "Salva modifiche".

            Q: Posso modificare sia il nome che il cognome contemporaneamente?
            A: Sì, puoi modificare entrambi i campi prima di cliccare su "Salva modifiche".

            Q: Come posso cambiare il mio dipartimento?
            A: Inserisci il nuovo dipartimento nel campo "Dipartimento" e
                conferma la modifica cliccando sul pulsante "Salva modifiche".

            Q: Come posso modificare il mio corso di studi?
            A: Inserisci il nuovo corso di studi nel campo "Corso di Studi" e
                conferma la modifica cliccando sul pulsante "Salva modifiche".

            Q: Come posso cambiare la mia immagine di profilo?
            A: Clicca sul pulsante "Modifica immagine", seleziona una nuova immagine
                dal tuo dispositivo e conferma la modifica.

            Q: Ci sono delle restrizioni per le immagini di profilo?
            A: Assicurati che l'immagine sia in un formato supportato (es. JPG, PNG) e
                che la dimensione del file non sia troppo grande.

            Q: Come faccio a confermare le modifiche nell'account?
            A: Dopo aver inserito i nuovi valori nei rispettivi campi,
                clicca sul pulsante "Salva modifiche" per confermare tutte le modifiche.

            Q: Posso ripristinare le informazioni originali del mio account?
            A: Non c'è un'opzione diretta per ripristinare le informazioni originali,
                dovrai modificare manualmente ogni campo per riportarlo ai valori originali.
            """;
}
