package it.unical.demacs.informatica.unicareermanager.model;

import com.calendarfx.model.Entry;
import it.unical.demacs.informatica.unicareermanager.util.DateUtils;
import it.unical.demacs.informatica.unicareermanager.util.EntryUtils;
import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import javafx.concurrent.Task;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {

    private Connection con = null;

    private static final DBConnection instance = new DBConnection();

    public static DBConnection getInstance() {
        return instance;
    }

    private DBConnection() {
        // connecting to the database
        try {
            String url = "jdbc:sqlite:unicareermanager.db";
            con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            // debug
            // e.printStackTrace();
        }
    }

    public boolean isConnected() {
        // checking the connection
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void close() {
        // closing the ExecutorService and the connection
        try {
            if (con != null)
                con.close();
        } catch (SQLException ignored) {
        }
    }

    public Task<Boolean> insertUser(String mat, String university, String password, String firstName, String lastName) {
        // inserting a new user checking its existence
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            if (isConnected()) {
                PreparedStatement checkStmt = con.prepareStatement("SELECT COUNT(*) FROM utenti WHERE matricola=? AND universita=?");
                checkStmt.setString(1, mat);
                checkStmt.setString(2, university);
                ResultSet rs = checkStmt.executeQuery();
                boolean notDuplicated = false;
                if (rs.next())
                    notDuplicated = rs.getInt(1) == 0;
                checkStmt.close();
                if (notDuplicated) {
                    PreparedStatement stmt = con.prepareStatement("INSERT INTO utenti(matricola,universita,password,nome,cognome) VALUES(?, ?, ?, ?, ?);");
                    stmt.setString(1, mat);
                    stmt.setString(2, university);
                    String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
                    stmt.setString(3, generatedSecuredPasswordHash);
                    stmt.setString(4, firstName);
                    stmt.setString(5, lastName);
                    stmt.executeUpdate();
                    stmt.close();
                }
                return notDuplicated;
            }
            return false;
        });
    }

    public Task<Boolean> checkPassword(String mat, String university, String password) {
        // checking the correctness of the password
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("SELECT password FROM utenti WHERE matricola=? AND universita=?;");
                stmt.setString(1, mat);
                stmt.setString(2, university);
                ResultSet rs = stmt.executeQuery();
                String passwordFound = rs.getString("password");
                boolean matched = false;
                if (rs.next()) {
                    matched = BCrypt.checkpw(password, passwordFound);
                }
                stmt.close();
                return matched;
            }
            return false;
        });
    }

    public Task<User> getUser(String mat, String university) {
        // getting a user from primary keys
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            User user = null;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM utenti WHERE matricola=? AND universita=?;");
                stmt.setString(1, mat);
                stmt.setString(2, university);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    user = new User(rs.getString(1), rs.getString(2), rs.getString(4), rs.getString(5), rs.getBytes(6), rs.getString(7), rs.getString(8));
                }
                stmt.close();
            }
            return user;
        });
    }

    public Task<User> updateUser(String firstName, String lastName, byte[] image, String department, String cds, User user) {
        // updating user
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            User newUser = null;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("UPDATE utenti SET nome=?, cognome=?, immagine=?, dipartimento=?, cds=? WHERE matricola=? AND universita=?;");
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setBytes(3, image);
                stmt.setString(4, department);
                stmt.setString(5, cds);
                stmt.setString(6, user.mat());
                stmt.setString(7, user.university());
                if (stmt.executeUpdate() > 0)
                    newUser = new User(user.mat(), user.university(), firstName, lastName, image, department, cds);
                stmt.close();
            }
            return newUser;
        });
    }

    public Task<Tax> insertTax(String taxName, String expirationDate, String payedDate, Double amount, User user) {
        // inserting a new tax
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Tax tax = null;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("INSERT INTO tasse(nome, scadenza, data_pagamento, importo, matricola, universita) VALUES(?, ?, ?, ?, ?, ?);");
                stmt.setString(1, taxName);
                stmt.setString(2, expirationDate);
                stmt.setString(3, payedDate);
                stmt.setDouble(4, amount);
                stmt.setString(5, user.mat());
                stmt.setString(6, user.university());
                // obtaining the id (primary key) used to create the new tax
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        tax = new Tax(id, taxName, expirationDate, payedDate, amount);
                    }
                }
                stmt.close();
            }
            return tax;
        });
    }

    public Task<List<Tax>> getTaxes(User user) {
        // obtain all the taxes of a user
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            List<Tax> taxes = new ArrayList<>();
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM tasse WHERE matricola=? AND universita=?;");
                stmt.setString(1, user.mat());
                stmt.setString(2, user.university());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    taxes.add(new Tax(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDouble((5))));
                }
                stmt.close();
            }
            return taxes;
        });
    }

    public Task<Boolean> removeTax(int id) {
        // removing a tax
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            boolean removed = false;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM tasse WHERE id=?;");
                stmt.setInt(1, id);
                removed = stmt.executeUpdate() > 0;
                stmt.close();
            }
            return removed;
        });
    }

    public Task<Tax> updateTax(int id, String taxName, String expirationDate, String payedDate, Double amount) {
        // updating values of a tax
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Tax tax = null;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("UPDATE tasse SET nome=?, scadenza=?, data_pagamento=?, importo=? WHERE id=?;");
                stmt.setString(1, taxName);
                stmt.setString(2, expirationDate);
                stmt.setString(3, payedDate);
                stmt.setDouble(4, amount);
                stmt.setInt(5, id);
                if (stmt.executeUpdate() > 0)
                    tax = new Tax(id, taxName, expirationDate, payedDate, amount);
                stmt.close();
            }
            return tax;
        });
    }

    public Task<Exam> insertExam(String examName, String date, Double credits, String grade, User user) {
        // inserting a new exam
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Exam exam = null;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("INSERT INTO esami(nome, data, crediti, voto, matricola, universita) VALUES(?, ?, ?, ?, ?, ?);");
                stmt.setString(1, examName);
                stmt.setString(2, date);
                stmt.setDouble(3, credits);
                stmt.setString(4, grade);
                stmt.setString(5, user.mat());
                stmt.setString(6, user.university());
                // obtaining the id (primary key) used to create the new exam
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        exam = new Exam(id, examName, date, credits, grade);
                    }
                }
                stmt.close();
            }
            return exam;
        });
    }

    public Task<List<Exam>> getExams(User user) {
        // obtain all the exams of a user
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            List<Exam> exams = new ArrayList<>();
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM esami WHERE matricola=? AND universita=?;");
                stmt.setString(1, user.mat());
                stmt.setString(2, user.university());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    exams.add(new Exam(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString((5))));
                }
                stmt.close();
            }
            return exams;
        });
    }

    public Task<Boolean> removeExam(int id) {
        // removing an exam
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            boolean removed = false;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM esami WHERE id=?;");
                stmt.setInt(1, id);
                removed = stmt.executeUpdate() > 0;
                stmt.close();
            }
            return removed;
        });
    }

    public Task<Exam> updateExam(int id, String examName, String date, Double credits, String grade) {
        // updating values of an exam
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Exam exam = null;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("UPDATE esami SET nome=?, data=?, crediti=?, voto=? WHERE id=?;");
                stmt.setString(1, examName);
                stmt.setString(2, date);
                stmt.setDouble(3, credits);
                stmt.setString(4, grade);
                stmt.setInt(5, id);
                if (stmt.executeUpdate() > 0)
                    exam = new Exam(id, examName, date, credits, grade);
                stmt.close();
            }
            return exam;
        });
    }

    private boolean SubjectNotDuplicated(String subjectName, User user) throws SQLException {
        boolean notDuplicated = false;
        if (isConnected()) {
            PreparedStatement checkStmt = con.prepareStatement("SELECT COUNT(*) FROM materie WHERE nome=? AND matricola=? AND universita=?");
            checkStmt.setString(1, subjectName);
            checkStmt.setString(2, user.mat());
            checkStmt.setString(3, user.university());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next())
                notDuplicated = rs.getInt(1) == 0;
            checkStmt.close();
        }
        return notDuplicated;
    }

    public Task<Subject> insertSubject(String subjectName, String professor, String color, User user) {
        // inserting a new subject
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Subject subject = null;
            if (isConnected() && SubjectNotDuplicated(subjectName, user)) {
                PreparedStatement stmt = con.prepareStatement("INSERT INTO materie(nome, professore, colore, matricola, universita) VALUES(?, ?, ?, ?, ?);");
                stmt.setString(1, subjectName);
                stmt.setString(2, professor);
                stmt.setString(3, color);
                stmt.setString(4, user.mat());
                stmt.setString(5, user.university());
                if (stmt.executeUpdate() > 0)
                    subject = new Subject(subjectName, professor, color);
                stmt.close();
            }
            return subject;
        });
    }

    public Task<List<Subject>> getSubjects(User user) {
        // obtain all the subjects of a user
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            List<Subject> subjects = new ArrayList<>();
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM materie WHERE matricola=? AND universita=?;");
                stmt.setString(1, user.mat());
                stmt.setString(2, user.university());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    subjects.add(new Subject(rs.getString(1), rs.getString(2), rs.getString(3)));
                }
                stmt.close();
            }
            return subjects;
        });
    }

    public Task<Boolean> removeSubject(String nome, User user) {
        // removing a subject
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            boolean removed = false;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM materie WHERE nome=? AND matricola=? AND universita=?;");
                stmt.setString(1, nome);
                stmt.setString(2, user.mat());
                stmt.setString(3, user.university());
                removed = stmt.executeUpdate() > 0;
                stmt.close();
            }
            return removed;
        });
    }

    public Task<Subject> updateSubject(String newSubjectName, String oldSubjectName, String professor, String color, User user) {
        // updating values of a subject
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            Subject subject = null;
            if (isConnected() && (newSubjectName.equals(oldSubjectName) || SubjectNotDuplicated(newSubjectName, user))) {
                PreparedStatement stmt = con.prepareStatement("UPDATE materie SET nome=?, professore=?, colore=? WHERE nome=? AND matricola=? AND universita=?;");
                stmt.setString(1, newSubjectName);
                stmt.setString(2, professor);
                stmt.setString(3, color);
                stmt.setString(4, oldSubjectName);
                stmt.setString(5, user.mat());
                stmt.setString(6, user.university());
                if (stmt.executeUpdate() > 0)
                    subject = new Subject(newSubjectName, professor, color);
                stmt.close();
            }
            return subject;
        });
    }

    public Task<StudentSettings> getSettings(User user) {
        // obtain settings of a user
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            StudentSettings settings = null;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("SELECT crediti_totali, valore_lode, formato_valuta, tema_predefinito FROM utenti WHERE matricola=? AND universita=?;");
                stmt.setString(1, user.mat());
                stmt.setString(2, user.university());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    settings = new StudentSettings(rs.getDouble(1), rs.getInt(2), rs.getString(3), ThemeModel.fromName(rs.getString(4)));
                }
                stmt.close();
            }
            return settings;
        });
    }

    public Task<StudentSettings> updateSettings(Double maxCredits, Integer gradeValue, String currency, ThemeModel theme, User user) {
        // updating values of settings
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            StudentSettings settings = null;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("UPDATE utenti SET crediti_totali=?, valore_lode=?, formato_valuta=?, tema_predefinito=? WHERE matricola=? AND universita=?;");
                stmt.setDouble(1, maxCredits);
                stmt.setInt(2, gradeValue);
                stmt.setString(3, currency);
                stmt.setString(4, theme.getName());
                stmt.setString(5, user.mat());
                stmt.setString(6, user.university());
                if (stmt.executeUpdate() > 0)
                    settings = new StudentSettings(maxCredits, gradeValue, currency, theme);
                stmt.close();
            }
            return settings;
        });
    }

    public Task<Boolean> insertEntry(Entry<?> entry, String calendar, User user) {
        // inserting a new event
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            boolean inserted = false;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("INSERT INTO eventi(id, titolo, luogo, intera_giornata, inizio, fine, regola, calendario, matricola, universita) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                stmt.setString(1, entry.getId());
                stmt.setString(2, entry.getTitle());
                stmt.setString(3, entry.getLocation());
                stmt.setBoolean(4, entry.isFullDay());
                stmt.setString(5, DateUtils.formatDateTime(entry.getStartAsLocalDateTime()));
                stmt.setString(6, DateUtils.formatDateTime(entry.getEndAsLocalDateTime()));
                stmt.setString(7, entry.getRecurrenceRule());
                stmt.setString(8, calendar);
                stmt.setString(9, user.mat());
                stmt.setString(10, user.university());
                inserted = stmt.executeUpdate() > 0;
                stmt.close();
            }
            return inserted;
        });
    }

    public Task<List<Entry<?>>> getEntries(User user) {
        // obtain all the events of a user
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            List<Entry<?>> entries = new ArrayList<>();
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM eventi WHERE matricola=? AND universita=?;");
                stmt.setString(1, user.mat());
                stmt.setString(2, user.university());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    entries.add(EntryUtils.createEntry(rs.getString(1), rs.getString(2), rs.getString(3), rs.getBoolean(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
                }
                stmt.close();
            }
            return entries;
        });
    }

    public Task<Boolean> removeEntry(String id, User user) {
        // removing an event
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            boolean removed = false;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM eventi WHERE id=? AND matricola=? AND universita=?;");
                stmt.setString(1, id);
                stmt.setString(2, user.mat());
                stmt.setString(3, user.university());
                removed = stmt.executeUpdate() > 0;
                stmt.close();
            }
            return removed;
        });
    }

    public Task<Boolean> updateEntry(Entry<?> entry, String calendar, User user) {
        // updating values of an event
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            boolean updated = false;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("UPDATE eventi SET titolo=?, luogo=?, intera_giornata=?, inizio=?, fine=?, regola=?, calendario=? WHERE id=? AND matricola=? AND universita=?;");
                stmt.setString(1, entry.getTitle());
                stmt.setString(2, entry.getLocation());
                stmt.setBoolean(3, entry.isFullDay());
                stmt.setString(4, DateUtils.formatDateTime(entry.getStartAsLocalDateTime()));
                stmt.setString(5, DateUtils.formatDateTime(entry.getEndAsLocalDateTime()));
                stmt.setString(6, entry.getRecurrenceRule());
                stmt.setString(7, calendar);
                stmt.setString(8, entry.getId());
                stmt.setString(9, user.mat());
                stmt.setString(10, user.university());
                updated = stmt.executeUpdate() > 0;
                stmt.close();
            }
            return updated;
        });
    }

    public Task<Boolean> deleteAllEntry(User user) {
        // removing all events of a user
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            boolean deleted = false;
            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM eventi WHERE matricola=? AND universita=?;");
                stmt.setString(1, user.mat());
                stmt.setString(2, user.university());
                deleted = stmt.executeUpdate() > 0;
                stmt.close();
            }
            return deleted;
        });
    }
    
}
