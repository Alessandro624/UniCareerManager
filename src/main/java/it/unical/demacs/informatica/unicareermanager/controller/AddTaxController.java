package it.unical.demacs.informatica.unicareermanager.controller;

import it.unical.demacs.informatica.unicareermanager.util.DateUtils;
import it.unical.demacs.informatica.unicareermanager.costants.Message;
import it.unical.demacs.informatica.unicareermanager.costants.Title;
import it.unical.demacs.informatica.unicareermanager.model.DBConnection;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import it.unical.demacs.informatica.unicareermanager.model.MainHandler;
import it.unical.demacs.informatica.unicareermanager.model.Tax;
import it.unical.demacs.informatica.unicareermanager.view.SceneHandler;
import it.unical.demacs.informatica.unicareermanager.view.TaxHandlerView;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.time.LocalDate;

public class AddTaxController implements AddEditController {

    @FXML
    private Button addTaxButton;

    @FXML
    private TextField amountTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private DatePicker expirationDateField;

    @FXML
    private TextField nameTextField;

    @FXML
    private CheckBox paidCheckBox;

    @FXML
    private DatePicker paidDateField;

    private Tax toEditTax = null;

    @FXML
    void handleAddTax(ActionEvent event) {
        // handling the add/edit tax action
        if (!validateInput())
            return;
        String taxName = nameTextField.getText();
        String expirationDate = DateUtils.formatDate(expirationDateField.getValue());
        String paidDate = paidCheckBox.isSelected() ? DateUtils.formatDate(paidDateField.getValue()) : null;
        double amount = Double.parseDouble(amountTextField.getText());
        if (toEditTax != null && !modifiedInput(taxName, expirationDate, paidDate, amount))
            return;
        Task<Tax> task;
        if (toEditTax == null)
            task = DBConnection.getInstance().insertTax(taxName, expirationDate, paidDate, amount, MainHandler.getInstance().getUser());
        else if (SceneHandler.getInstance().showConfirmationMessage(Title.EDIT_CONFIRMATION, Message.EDIT_TAX_CONFIRMATION))
            task = DBConnection.getInstance().updateTax(toEditTax.id(), taxName, expirationDate, paidDate, amount);
        else
            task = null;
        if (task != null) {
            task.setOnSucceeded(e -> handleTaskSuccess(task.getValue()));
            task.setOnFailed(e -> {
                // debug
                // task.getException().printStackTrace();
                handleTaskFailure();
            });
            ExecutorServiceManager.getInstance().getExecutorService().submit(task);
        }
    }

    private boolean modifiedInput(String taxName, String expirationDate, String paidDate, double amount) {
        // checking if at least one field has been modified
        if (taxName.equals(toEditTax.taxName()) && expirationDate.equals(toEditTax.expirationDate()) && amount == toEditTax.amount()) {
            if ((paidDate == null && toEditTax.paidDate() == null) || (paidDate != null && toEditTax.paidDate() != null && paidDate.equals(toEditTax.paidDate()))) {
                errorLabel.setText(Message.NOT_EDITED);
                return false;
            }
        }
        return true;
    }

    private boolean validateInput() {
        // checking the validity of the fields
        if (nameTextField.getText().isBlank() || expirationDateField.getValue() == null || amountTextField.getText().isBlank()) {
            errorLabel.setText(Message.ALL_FIELDS_ARE_MANDATORY);
            return false;
        }
        if (paidCheckBox.isSelected() && paidDateField.getValue() == null) {
            errorLabel.setText(Message.PAID_DATE_FIELDS_MANDATORY);
            return false;
        }
        try {
            double amount = Double.parseDouble(amountTextField.getText());
            if (amount <= 0) {
                errorLabel.setText(Message.AMOUNT_GREATER_THAN_ZERO);
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText(Message.INVALID_FORMAT_AMOUNT);
            return false;
        }
        return true;
    }

    private void handleTaskSuccess(Tax tax) {
        if (tax == null) {
            handleTaskFailure();
            return;
        }
        // handling success of a task
        // toEditTax == null --> insert a new tax
        // toEditTax != null --> edit the passed tax
        if (toEditTax == null) {
            TaxHandlerView.getInstance().addTax(tax, tax.paidDate() != null);
            clearFields();
        } else {
            TaxHandlerView.getInstance().removeTax(toEditTax, toEditTax.paidDate() != null);
            TaxHandlerView.getInstance().addTax(tax, tax.paidDate() != null);
            toEditTax = tax;
            errorLabel.setText("");
        }
        SceneHandler.getInstance().showInfoMessage((toEditTax == null) ? Title.ADD_INFO : Title.EDIT_INFO, (toEditTax == null) ? Message.ADDED_TAX : Message.EDITED_TAX);
    }

    private void handleTaskFailure() {
        // handling task failure
        SceneHandler.getInstance().showErrorMessage(Title.TAX_ERROR, (toEditTax == null) ? Message.ADD_TAX_ERROR : Message.EDIT_TAX_ERROR, false);
    }

    @FXML
    void handleCheckBox(ActionEvent event) {
        // isSelected == true --> show paidDateField
        // isSelected == false --> hide paidDateField
        boolean isSelected = paidCheckBox.isSelected();
        paidDateField.setDisable(!isSelected);
        paidDateField.setFocusTraversable(isSelected);
    }

    @Override
    public void setEditItem(Object item) throws Exception {
        // to transform the add tax scene in edit mode with the information of the tax to edit
        if (item instanceof Tax tax) {
            toEditTax = tax;
            nameTextField.setText(tax.taxName());
            expirationDateField.setValue(DateUtils.parseDate(tax.expirationDate()));
            if (tax.paidDate() != null) {
                paidCheckBox.setSelected(true);
                paidDateField.setDisable(false);
                paidDateField.setFocusTraversable(true);
                paidDateField.setValue(DateUtils.parseDate(tax.paidDate()));
            }
            amountTextField.setText(tax.amount().toString());
            addTaxButton.setText("Modifica tassa");
        } else
            throw new Exception("Item must be an istance of Tax");
    }

    private void setUpDatePicker() {
        // handling problem with focus/hover/action on data picker
        expirationDateField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER && !expirationDateField.isShowing()) {
                expirationDateField.show();
            }
        });
        paidDateField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER && !paidDateField.isShowing()) {
                paidDateField.show();
            }
        });
        // setting the paidDateField to hide dates after the current date
        paidDateField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
    }

    private void clearFields() {
        // clearing text from fields
        errorLabel.setText("");
        nameTextField.clear();
        expirationDateField.setValue(null);
        paidDateField.setValue(null);
        amountTextField.clear();
    }

    private void setUpStyle() {
        // styling the error label
        errorLabel.getStyleClass().add("error-label");
    }

    @FXML
    void initialize() {
        setUpStyle();
        clearFields();
        setUpDatePicker();
    }
}
