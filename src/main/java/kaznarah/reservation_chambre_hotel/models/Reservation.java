package kaznarah.reservation_chambre_hotel.models;

import java.sql.Date;

public class Reservation {
    private int nbre_jour;
    private int id_reservation;
    private String votre_nom  ;
    private String votre_mail;
    private String num_chambre;
    private Date date;
    private Date date_entre;



    public Reservation(int idReservation, String numChambre, Date date, Date dateEntree, int nbrJour, String nomClient, String mail) {
        this.id_reservation = idReservation;
        this.num_chambre = numChambre;
        this.date = date;
        this.date_entre = dateEntree;
        this.nbre_jour = nbrJour;
        this.votre_nom = nomClient;
        this.votre_mail = mail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate_entre() {
        return date_entre;
    }

    public void setDate_entre(Date date_entre) {
        this.date_entre = date_entre;
    }

    private int getNombre_jour;



    public int getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(int id_reservation) {
        this.id_reservation = id_reservation;
    }

    public String getVotre_nom() {
        return votre_nom;
    }

    public void setVotre_nom(String votre_nom) {
        this.votre_nom = votre_nom;
    }

    public String getVotre_mail() {
        return votre_mail;
    }

    public void setVotre_mail(String votre_mail) {
        this.votre_mail = votre_mail;
    }

    public String getNum_chambre() {
        return num_chambre;
    }

    public void setNum_chambre(String num_chambre) {
        this.num_chambre = num_chambre;
    }

    public int getNbre_jour() {
        return nbre_jour;
    }

    public void setNbre_jour(int nbre_jour) {
        this.nbre_jour = nbre_jour;
    }

    public String getText() {
        return null;
    }

    public void setText(String s) {
    }
}
