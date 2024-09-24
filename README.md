# UniCareerManager

## Introduzione

UniCareerManager è un'applicazione JavaFX progettata per gestire le carriere universitarie degli studenti. Questa
applicazione consente agli studenti di tenere traccia dei loro corsi, voti e progressi accademici in modo semplice e
intuitivo.

## Autori

- Sanò Gabriele -> mat: 239873
- Sidero Alessandro -> mat: 239525
- Vecchio Domenico -> mat: 240414

## Divisione del lavoro

- Sanò Gabriele
    - java
        - controller
            - AccountController
            - AddSubjectController
            - SettingController
            - SubjectController

        - model
            - LogoutHandler
            - StudentSettings
            - StudentSettingsProperties
            - Subject

        - view
            - CurrencyHandlerView
            - SubjectHandlerView
            - SubjectListCell

    - resources
        - view
            - account-view.fxml
            - add-subject-view.fxml
            - settings-view.fxml
            - subject-view.fxml


- Sidero Alessandro
    - java
        - controller
            - AddExamController
            - AddTaxController
            - AgendaController
            - StudentCardController
            - TaxController

        - model
            - CustomEntryComparator
            - EntrySet
            - Exam
            - ExamStatistics
            - ExamStatisticsProperties
            - ExamStatisticsService
            - Tax
            - TaxStatistics
            - TaxStatisticsProperties
            - TaxStatisticsService

        - util
            - EntryUtils

        - view
            - AgendaHandlerView
            - ButtonTableCell
            - CustomAgendaView
            - CustomCalendarSource
            - CustomCalendarView
            - ExamHandlerView
            - TaxHandlerView

    - resources
        - css
            - my-calendar.css

        - view
            - add-exam-view.fxml
            - add-tax-view.fxml
            - agenda-view.fxml
            - student-card-view.fxml
            - tax-view.fxml


- Vecchio Domenico
    - java
        - controller
            - ChartController
            - PrevisionController

        - model
            - ChartsHandler
            - ExamPrevision
            - ExamPrevisionService

    - resources
        - view
            - chart-view.fxml
            - prevision-view.fxml


- In comune
    - java
        - controller
            - AddEditController
            - HomeController
            - LoginController
            - MainController

        - costants

        - model
            - CentralPanesModel
            - DBConnection
            - LoadCentralPanesHandler
            - MainHandler
            - ThemeModel
            - UniversityDownloaderHandler
            - User
            - UserProperties

        - util
            - ColorUtils
            - DateUtils
            - ExecutorServiceManager

        - view
            - AutoCompleteTextField
            - CustomHelpTextArea
            - EditDeleteBox
            - SceneHandler

        - Main
        - MainApplication

    - resources
        - css
            - dark.css
            - light.css
            - fonts.css
            - my-style.css

        - fonts

        - images

        - logos

        - view
            - home-view.fxml
            - login-view.fxml
            - main-view.fxml

## Utilizzo

### Accesso Profili Predefiniti

Per mostrare il funzionamento dell'applicazione, sono stati creati alcuni profili predefiniti. Utilizzare le seguenti
credenziali per accedere:

- Profilo 1
    - Matricola: 000001
    - Università: Università degli studi di Torino
    - Password: Password1

- Profilo 2
    - Matricola: 000002
    - Università: Università degli Studi di Milano
    - Password: Password2

- Profilo 3
    - Matricola: 000003
    - Università: Università della Calabria
    - Password: Password3

## Dipendenze

- [CalendarFX](https://github.com/dlsc-software-consulting-gmbh/CalendarFX): Utilizzato per gestire e visualizzare gli
  eventi accademici nel calendario.

- [Hipolabs University Domain API](https://github.com/Hipo/university-domains-list): Presente, ma non utilizzato (utile
  per ottenere informazioni sulle università per la creazione di profili e la gestione dei dati).

- [MyMemory Translation API](https://mymemory.translated.net/): Presente, ma non utilizzato (utile per tradurre i nomi
  delle università).

- [MUR API](https://dati-ustat.mur.gov.it/dataset/metadati/resource/820aefe6-0662-4656-84ec-d8859a2a3b7e): Utilizzato
  per ottenere dati ufficiali sulle università italiane.
