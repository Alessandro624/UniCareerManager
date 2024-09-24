package it.unical.demacs.informatica.unicareermanager.model;

public record User(String mat, String university, String firstName, String lastName, byte[] image,
                   String department, String cds) {
}
