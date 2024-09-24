package it.unical.demacs.informatica.unicareermanager.view;

import it.unical.demacs.informatica.unicareermanager.model.Subject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class SubjectHandlerView {
    private final ObservableList<Subject> subjects = FXCollections.observableArrayList();

    private final static SubjectHandlerView instance = new SubjectHandlerView();

    private final List<ListChangeListener<? super Subject>> changeListeners = new ArrayList<>();

    private boolean loaded = false;

    private SubjectHandlerView() {
    }

    public static SubjectHandlerView getInstance() {
        return instance;
    }

    public ObservableList<Subject> getSubjects() {
        return FXCollections.unmodifiableObservableList(subjects);
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        subjects.remove(subject);
    }

    public void addListener(ListChangeListener<? super Subject> subjectListChangeListener) {
        changeListeners.add(subjectListChangeListener);
        if (loaded)
            subjects.addListener(subjectListChangeListener);
    }

    private void triggerChange() {
        // to trigger a change
        subjects.setAll(new ArrayList<>(subjects));
    }

    public void loadCompleted() {
        if (!loaded) {
            changeListeners.forEach(subjects::addListener);
            triggerChange();
            loaded = true;
        }
    }

    public void logout() {
        changeListeners.forEach(subjects::removeListener);
        changeListeners.clear();
        loaded = false;
        Platform.runLater(subjects::clear);
    }
}
