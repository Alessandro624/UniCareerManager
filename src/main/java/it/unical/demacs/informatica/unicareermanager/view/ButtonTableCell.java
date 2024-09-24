package it.unical.demacs.informatica.unicareermanager.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;

public class ButtonTableCell<T> extends TableCell<T, Void> {
    private final EditDeleteBox editDeleteBox = new EditDeleteBox();
    private T item;

    public ButtonTableCell() {
    }

    public void setOnDeleteAction(EventHandler<ActionEvent> handler) {
        editDeleteBox.setOnDeleteAction(actionEvent -> {
            getTableView().getSelectionModel().select(item);
            handler.handle(actionEvent);
        });
    }

    public void setOnEditAction(EventHandler<ActionEvent> handler) {
        editDeleteBox.setOnEditAction(actionEvent -> {
            getTableView().getSelectionModel().select(item);
            handler.handle(actionEvent);
        });
    }

    public T getSelectedItem() {
        return item;
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            this.item = null;
        } else {
            setGraphic(editDeleteBox);
            this.item = getTableView().getItems().get(getIndex());
        }
    }
}
