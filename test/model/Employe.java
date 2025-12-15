package model;

import java.util.ArrayList;
import java.util.List;

public class Employe {
    private String nom;
    private String genre;
    private List<Departement> departements;

    // Constructeur par défaut
    public Employe() {
        this.departements = new ArrayList<>();
    }

    // Constructeur avec paramètres
    public Employe(String nom, String genre, List<Departement> departements) {
        this.nom = nom;
        this.genre = genre;
        this.departements = departements != null ? departements : new ArrayList<>();
    }

    // Getters et setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<Departement> getDepartements() {
        return departements;
    }

    public void setDepartements(List<Departement> departements) {
        this.departements = departements;
    }
    
    public void addDepartement(Departement departement) {
        if (this.departements == null) {
            this.departements = new ArrayList<>();
        }
        this.departements.add(departement);
    }
}
