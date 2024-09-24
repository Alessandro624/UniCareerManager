package it.unical.demacs.informatica.unicareermanager.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteTextField extends TextField {
    private final ObservableList<String> suggestionsList = FXCollections.observableArrayList();
    private final ContextMenu suggestionsContextMenu = new ContextMenu();

    public AutoCompleteTextField() {
        super();
        this.setPrefWidth(200);
        this.setPrefHeight(30);
        this.textProperty().addListener((observable, oldValue, newValue) -> handleSuggestions(newValue));
    }

    public void setSuggestions(List<String> suggestions) {
        if (suggestions == null)
            suggestions = new ArrayList<>();
        suggestionsList.setAll(suggestions);
    }

    private void handleSuggestions(String text) {
        // handling suggestions to show
        if (text.isBlank() || text.length() < 3 || suggestionsList.isEmpty()) {
            suggestionsContextMenu.hide();
            return;
        }
        List<String> filteredSuggestions = suggestionsList.stream()
                .filter(item -> item.toLowerCase().contains(text.toLowerCase()))
                .limit(10)
                .toList();
        if (filteredSuggestions.isEmpty()) {
            suggestionsContextMenu.hide();
            return;
        }
        suggestionsContextMenu.getItems().clear();
        filteredSuggestions.forEach(suggestion -> {
            MenuItem menuItem = new MenuItem(suggestion);
            menuItem.setOnAction(actionEvent -> {
                this.setText(suggestion);
                this.positionCaret(suggestion.length());
                suggestionsContextMenu.hide();
                actionEvent.consume();
            });
            suggestionsContextMenu.getItems().add(menuItem);
        });
        if (!suggestionsContextMenu.isShowing()) {
            suggestionsContextMenu.show(this, Side.BOTTOM, 0, 0);
        }
    }
}
