package it.unical.demacs.informatica.unicareermanager.view;

import it.unical.demacs.informatica.unicareermanager.model.Subject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SubjectListCell extends ListCell<Subject> {
    private final HBox hBox = new HBox();
    private final Rectangle colorRect = new Rectangle(20, 50);
    private final Label nameLabel = new Label();
    private final Label professorLabel = new Label();
    private final EditDeleteBox editDeleteBox = new EditDeleteBox();
    private Subject subject = null;

    public SubjectListCell() {
        VBox labelBox = new VBox(nameLabel, professorLabel);
        labelBox.setSpacing(5);
        HBox.setHgrow(labelBox, Priority.ALWAYS);
        hBox.getChildren().addAll(colorRect, labelBox, editDeleteBox);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
    }

    public void setOnDeleteAction(EventHandler<ActionEvent> handler) {
        editDeleteBox.setOnDeleteAction(actionEvent -> {
            getListView().getSelectionModel().select(subject);
            handler.handle(actionEvent);
        });
    }

    public void setOnEditAction(EventHandler<ActionEvent> handler) {
        editDeleteBox.setOnEditAction(actionEvent -> {
            getListView().getSelectionModel().select(subject);
            handler.handle(actionEvent);
        });
    }

    public Subject getSelectedSubject() {
        return subject;
    }

    private void setLabels() {
        colorRect.setFill(Color.web(subject.color()));
        nameLabel.setText(subject.subjectName());
        professorLabel.setText(subject.professor());
    }

    @Override
    protected void updateItem(Subject subject, boolean empty) {
        super.updateItem(subject, empty);
        if (empty || subject == null) {
            setText(null);
            setGraphic(null);
        } else {
            this.subject = subject;
            setLabels();
            setGraphic(hBox);
        }
    }
}
