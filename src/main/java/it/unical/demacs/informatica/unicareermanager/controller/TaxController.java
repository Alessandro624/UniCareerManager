package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.*;
import it.unical.demacs.informatica.unicareermanager.util.DateUtils;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.view.ButtonTableCell;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import it.unical.demacs.informatica.unicareermanager.view.TaxHandlerView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class TaxController {

    @FXML
    private Button addTaxButton;

    @FXML
    private TableColumn<Tax, String> paidAmountColumn;

    @FXML
    private TableColumn<Tax, String> paidDateColumn;

    @FXML
    private TableColumn<Tax, Void> paidDeleteEditColumn;

    @FXML
    private TableColumn<Tax, String> paidExpirationDateColumn;

    @FXML
    private ChoiceBox<String> paidFilterChoiceBox;

    @FXML
    private TextField paidFilterTextField;

    @FXML
    private Label paidLabel;

    @FXML
    private TableView<Tax> paidTableView;

    @FXML
    private TableColumn<Tax, String> paidTaxNameColumn;

    @FXML
    private TabPane tabPane;

    @FXML
    private Label taxLabel;

    @FXML
    private TableColumn<Tax, String> toPayAmountColumn;

    @FXML
    private TableColumn<Tax, Void> toPayDeleteEditColumn;

    @FXML
    private TableColumn<Tax, String> toPayExpirationDateColumn;

    @FXML
    private ChoiceBox<String> toPayFilterChoiceBox;

    @FXML
    private TextField toPayFilterTextField;

    @FXML
    private Label toPayLabel;

    @FXML
    private TableView<Tax> toPayTableView;

    @FXML
    private TableColumn<Tax, String> toPayTaxNameColumn;

    private final TaxStatisticsService service = new TaxStatisticsService();

    private static final String TAX_NAME = "Nome tassa";
    private static final String PAID_DATE = "Data pagamento";
    private static final String EXIPARTION_DATE = "Scadenza";
    private static final String AMOUNT = "Importo";

    @FXML
    void handleAddTaxButton(ActionEvent event) {
        // opening the add tax scene
        try {
            SceneHandler.getInstance().setAddEditScene("add-tax-view.fxml", Title.ADD_TAX_TITLE, null);
        } catch (Exception e) {
            // debug
            // e.printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.TAX_ERROR, Message.ADD_TAX_ERROR, false);
        }
    }

    private void setUpLabels() {
        // binding values of the labels
        StringBinding amountBinding = Bindings.createStringBinding(() ->
                        String.format("Importo (%s)", StudentSettingsProperties.getInstance().getCurrency()),
                StudentSettingsProperties.getInstance().currencyProperty());
        paidAmountColumn.textProperty().bind(amountBinding);
        toPayAmountColumn.textProperty().bind(amountBinding);
        paidLabel.textProperty().bind(Bindings.createStringBinding(() ->
                        String.format(Locale.US, "Importo totale pagato: %.2f %s",
                                TaxStatisticsProperties.getInstance().getTotalPaid(),
                                StudentSettingsProperties.getInstance().getCurrency()),
                TaxStatisticsProperties.getInstance().totalPaidProperty(),
                StudentSettingsProperties.getInstance().currencyProperty()));
        toPayLabel.textProperty().bind(Bindings.createStringBinding(() ->
                        String.format(Locale.US, "Importo totale da pagare: %.2f %s",
                                TaxStatisticsProperties.getInstance().getTotalToPay(),
                                StudentSettingsProperties.getInstance().getCurrency()),
                TaxStatisticsProperties.getInstance().totalToPayProperty(),
                StudentSettingsProperties.getInstance().currencyProperty()));
    }

    private void setFilteredTaxes(TableView<Tax> tableView, TextField textField, ChoiceBox<String> choiceBox, ObservableList<Tax> taxes) {
        // setting up filters
        FilteredList<Tax> filteredList = new FilteredList<>(taxes, p -> true);
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            String lowerCaseFilter = (newValue == null || newValue.isBlank()) ? "" : newValue.toLowerCase();
            filteredList.setPredicate(tax -> {
                // display all if filter is empty
                if (lowerCaseFilter.isBlank())
                    return true;
                switch (choiceBox.getValue()) {
                    case TAX_NAME -> {
                        return tax.taxName().toLowerCase().contains(lowerCaseFilter);
                    }
                    case PAID_DATE -> {
                        return tax.paidDate().toLowerCase().contains(lowerCaseFilter);
                    }
                    case EXIPARTION_DATE -> {
                        return tax.expirationDate().toLowerCase().contains(lowerCaseFilter);
                    }
                    case AMOUNT -> {
                        return String.format(Locale.US, "%.2f", tax.amount()).startsWith(lowerCaseFilter);
                    }
                    default -> {
                        return false;
                    }
                }
            });
        }));
        SortedList<Tax> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    private void update() {
        // updating labels
        service.setPaidTaxes(TaxHandlerView.getInstance().getPaidTaxes());
        service.setToPayTaxes(TaxHandlerView.getInstance().getToPayTaxes());
        service.restart();
    }

    private void loadTaxes() {
        // load taxes
        Task<List<Tax>> task = DBConnection.getInstance().getTaxes(MainHandler.getInstance().getUser());
        task.setOnSucceeded(e -> {
            for (Tax t : task.getValue()) {
                TaxHandlerView.getInstance().addTax(t, t.paidDate() != null);
            }
            update();
            TaxHandlerView.getInstance().addListener((ListChangeListener<? super Tax>) change -> {
                update();
                if (change.next()) {
                    if (change.wasAdded()) {
                        if (!toPayTableView.getItems().isEmpty())
                            toPayTableView.getSelectionModel().select(toPayTableView.getItems().getLast());
                        if (!paidTableView.getItems().isEmpty())
                            paidTableView.getSelectionModel().select(paidTableView.getItems().getLast());
                    }
                    if (change.wasRemoved()) {
                        toPayTableView.getSelectionModel().clearSelection();
                        paidTableView.getSelectionModel().clearSelection();
                    }
                }
            });
            setFilteredTaxes(paidTableView, paidFilterTextField, paidFilterChoiceBox, TaxHandlerView.getInstance().getPaidTaxes());
            setFilteredTaxes(toPayTableView, toPayFilterTextField, toPayFilterChoiceBox, TaxHandlerView.getInstance().getToPayTaxes());
            TaxHandlerView.getInstance().loadCompleted();
            // to prevent strange positioning of the scrollbar
            toPayTableView.autosize();
            paidTableView.autosize();
        });
        task.setOnFailed(e -> {
            // debug
            // task.getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.TAX_ERROR, Message.LOAD_TAX_ERROR, true);
        });
        ExecutorServiceManager.getInstance().getExecutorService().submit(task);
    }

    private void setUpService() {
        // setting up service
        service.setOnSucceeded(e -> {
            paidTableView.refresh();
            toPayTableView.refresh();
            if (e.getSource().getValue() instanceof TaxStatistics taxStatistics) {
                TaxStatisticsProperties.getInstance().setTotalPaid(taxStatistics.totalPaid());
                TaxStatisticsProperties.getInstance().setTotalToPay(taxStatistics.totalToPay());
            }
        });
        service.setOnFailed(e -> {
            // debug
            // e.getSource().getException().printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.TAX_ERROR, Message.UPDATE_ERROR, true);
        });
    }

    private void handleClearSelection(MouseEvent event) {
        // clearing selection
        if (!(event.getTarget() instanceof TableRow<?>)) {
            toPayTableView.getSelectionModel().clearSelection();
            paidTableView.getSelectionModel().clearSelection();
        }
    }

    private void setUpTableView() {
        // setting up table view items and handling action on the action of add/edit/delete of items
        paidTableView.setPlaceholder(new Label("Nessuna tassa pagata disponibile"));
        toPayTableView.setPlaceholder(new Label("Nessuna tassa da pagare disponibile"));
        paidTableView.sceneProperty().addListener(((observable, oldScene, newScene) -> {
            if (newScene != null)
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleClearSelection);
        }));
        toPayTableView.sceneProperty().addListener(((observable, oldScene, newScene) -> {
            if (newScene != null)
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleClearSelection);
        }));
    }

    private void setUpDateComparator(TableColumn<Tax, String> column) {
        // setting up date comparator
        column.setComparator((String value1, String value2) -> {
            try {
                LocalDate dateValue1 = DateUtils.parseDate(value1);
                LocalDate dateValue2 = DateUtils.parseDate(value2);
                return dateValue1.compareTo(dateValue2);
            } catch (Exception e) {
                return value1.compareTo(value2);
            }
        });
    }

    private void setUpDoubleComparator(TableColumn<Tax, String> column) {
        // setting up double comparator for amount
        column.setComparator((String value1, String value2) -> {
            try {
                double doubleValue1 = Double.parseDouble(value1);
                double doubleValue2 = Double.parseDouble(value2);
                return Double.compare(doubleValue1, doubleValue2);
            } catch (NumberFormatException e) {
                return value1.compareTo(value2);
            }
        });
    }

    private void editItem(Tax tax) {
        // handling the edit action of a tax (using the add tax scene with a parameter)
        try {
            SceneHandler.getInstance().setAddEditScene("add-tax-view.fxml", Title.EDIT_TAX_TITLE, tax);
        } catch (Exception e) {
            // debug
            // e.printStackTrace();
            SceneHandler.getInstance().showErrorMessage(Title.TAX_ERROR, Message.EDIT_TAX_ERROR, false);
        }
    }

    private void deleteItem(Tax tax) {
        // handling the delete action of a tax
        if (SceneHandler.getInstance().showConfirmationMessage(Title.REMOVE_CONFIRMATION, Message.REMOVE_TAX_CONFIRMATION)) {
            Task<Boolean> task = DBConnection.getInstance().removeTax(tax.id());
            task.setOnSucceeded(e -> {
                if (task.getValue())
                    TaxHandlerView.getInstance().removeTax(tax, tax.paidDate() != null);
                else
                    SceneHandler.getInstance().showErrorMessage(Title.TAX_ERROR, Message.REMOVE_TAX_ERROR, false);
            });
            task.setOnFailed(e -> {
                // debug
                // task.getException().printStackTrace();
                SceneHandler.getInstance().showErrorMessage(Title.TAX_ERROR, Message.REMOVE_TAX_ERROR, false);
            });
            ExecutorServiceManager.getInstance().getExecutorService().submit(task);
        }
    }

    private void setUpDeleteEditButtonCell(TableColumn<Tax, Void> column) {
        // setting up delete and edit column (CellFactory)
        column.setCellFactory(param -> {
            ButtonTableCell<Tax> deleteEditCell = new ButtonTableCell<>();
            deleteEditCell.setOnDeleteAction(event -> deleteItem(deleteEditCell.getSelectedItem()));
            deleteEditCell.setOnEditAction(event -> editItem(deleteEditCell.getSelectedItem()));
            return deleteEditCell;
        });
    }

    private void setUpColumns() {
        // setting up columns with tax values
        paidTaxNameColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().taxName()));
        paidExpirationDateColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().expirationDate()));
        paidDateColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().paidDate()));
        paidAmountColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(String.format(Locale.US, "%.2f", s.getValue().amount())));
        toPayTaxNameColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().taxName()));
        toPayExpirationDateColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(s.getValue().expirationDate()));
        toPayAmountColumn.setCellValueFactory(s -> new ReadOnlyStringWrapper(String.format(Locale.US, "%.2f", s.getValue().amount())));
        setUpDeleteEditButtonCell(toPayDeleteEditColumn);
        setUpDeleteEditButtonCell(paidDeleteEditColumn);
        setUpDoubleComparator(toPayAmountColumn);
        setUpDoubleComparator(paidAmountColumn);
        setUpDateComparator(toPayExpirationDateColumn);
        setUpDateComparator(paidExpirationDateColumn);
        setUpDateComparator(paidDateColumn);
    }

    private void setUpChoiceBox() {
        // setting up choice box
        paidFilterChoiceBox.getItems().addAll(TAX_NAME, PAID_DATE, EXIPARTION_DATE, AMOUNT);
        paidFilterChoiceBox.setValue(TAX_NAME);
        toPayFilterChoiceBox.getItems().addAll(TAX_NAME, EXIPARTION_DATE, AMOUNT);
        toPayFilterChoiceBox.setValue(TAX_NAME);
        paidFilterChoiceBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !paidFilterChoiceBox.isShowing()) {
                paidFilterChoiceBox.show();
            }
        });
        toPayFilterChoiceBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !toPayFilterChoiceBox.isShowing()) {
                toPayFilterChoiceBox.show();
            }
        });
    }

    private void setUpTabs() {
        // setting up dimensions of tabs
        tabPane.tabMinWidthProperty()
                .bind(tabPane.widthProperty()
                        .divide(tabPane.getTabs()
                                .size()).subtract(20));
    }

    private void applyStyles() {
        // styling labels
        taxLabel.getStyleClass().add("bold-label");
        paidLabel.getStyleClass().add("bold-label");
        toPayLabel.getStyleClass().add("bold-label");
    }

    @FXML
    void initialize() {
        applyStyles();
        setUpTabs();
        setUpChoiceBox();
        setUpColumns();
        setUpTableView();
        setUpService();
        loadTaxes();
        setUpLabels();
    }
}
