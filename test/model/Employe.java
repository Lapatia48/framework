package model;

public class Employe {
    private String nom;
    private String genre;
    private int idDepartement;

    // Constructeur par défaut
    public Employe() {}

    // Constructeur avec paramètres
    public Employe(String nom, String genre, int idDepartement) {
        this.nom = nom;
        this.genre = genre;
        this.idDepartement = idDepartement;
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

    public int getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(int idDepartement) {
        this.idDepartement = idDepartement;
    }
}
