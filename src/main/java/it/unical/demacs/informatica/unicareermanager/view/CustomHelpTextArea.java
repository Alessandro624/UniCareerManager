package it.unical.demacs.informatica.unicareermanager.view;

import javafx.scene.control.TextArea;

public class CustomHelpTextArea extends TextArea {
    public CustomHelpTextArea(String content) {
        super(content);
        this.setWrapText(true);
        this.setEditable(false);
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }
}
