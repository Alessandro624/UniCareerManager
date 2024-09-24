package it.unical.demacs.informatica.unicareermanager.model;

public record Exam(int id, String examName, String date, Double credits, String grade) {
    public int getGradeAsInteger() {
        // to get an Integer version of grade
        if (grade.equalsIgnoreCase("idoneità")) {
            return 0;
        } else if (grade.equalsIgnoreCase("30 e lode")) {
            return StudentSettingsProperties.getInstance().getGradeValue();
        } else {
            try {
                return Integer.parseInt(grade);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }
}
