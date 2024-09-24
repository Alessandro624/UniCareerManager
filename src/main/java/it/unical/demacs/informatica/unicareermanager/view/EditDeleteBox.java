package it.unical.demacs.informatica.unicareermanager.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class EditDeleteBox extends HBox {
    private final Button deleteButton = new Button();
    private final Button editButton = new Button();

    public EditDeleteBox() {
        FontIcon deleteFontIcon = new FontIcon("far-trash-alt");
        FontIcon editFontIcon = new FontIcon("fas-pen");
        deleteButton.setGraphic(deleteFontIcon);
        editButton.setGraphic(editFontIcon);
        deleteButton.setTooltip(new Tooltip("elimina"));
        editButton.setTooltip(new Tooltip("modifica"));
        deleteFontIcon.getStyleClass().add("icons-color");
        editFontIcon.getStyleClass().add("icons-color");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.getChildren().addAll(editButton, deleteButton);
    }

    public void setOnDeleteAction(EventHandler<ActionEvent> handler) {
        deleteButton.setOnAction(handler);
    }

    public void setOnEditAction(EventHandler<ActionEvent> handler) {
        editButton.setOnAction(handler);
    }
}
