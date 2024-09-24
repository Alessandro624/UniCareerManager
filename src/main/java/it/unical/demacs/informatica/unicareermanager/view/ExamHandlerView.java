package it.unical.demacs.informatica.unicareermanager.view;

import it.unical.demacs.informatica.unicareermanager.model.Exam;
import it.unical.demacs.informatica.unicareermanager.model.StudentSettingsProperties;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ExamHandlerView {
    private final ObservableList<Exam> exams = FXCollections.observableArrayList();

    private final static ExamHandlerView instance = new ExamHandlerView();

    private final List<ListChangeListener<? super Exam>> changeListeners = new ArrayList<>();

    private boolean loaded = false;

    private ExamHandlerView() {
        StudentSettingsProperties.getInstance().gradeValueProperty().addListener((obs, oldVal, newVal) -> triggerChange());
    }

    private void triggerChange() {
        // to trigger a change
        exams.setAll(new ArrayList<>(exams));
    }

    public static ExamHandlerView getInstance() {
        return instance;
    }

    public ObservableList<Exam> getExams() {
        return FXCollections.unmodifiableObservableList(exams);
    }
    
    public void addExam(Exam exam) {
        exams.add(exam);
    }

    public void removeExam(Exam exam) {
        exams.remove(exam);
    }

    public void addListener(ListChangeListener<? super Exam> examListChangeListener) {
        changeListeners.add(examListChangeListener);
        if (loaded) {
            exams.addListener(examListChangeListener);
        }
    }

    public void loadCompleted() {
        if (!loaded) {
            changeListeners.forEach(exams::addListener);
            triggerChange();
            loaded = true;
        }
    }

    public void logout() {
        changeListeners.forEach(exams::removeListener);
        changeListeners.clear();
        loaded = false;
        Platform.runLater(exams::clear);
    }
}
