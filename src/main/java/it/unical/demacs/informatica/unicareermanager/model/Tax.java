package it.unical.demacs.informatica.unicareermanager.model;

public record Tax(int id, String taxName, String expirationDate, String paidDate, Double amount) {
}
