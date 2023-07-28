package kaznarah.reservation_chambre_hotel.models;

import java.sql.Date;

public class Sejour {
    private int id_sejour;
    private String id_numeri_chambre;
    private Date date_entre;
    private int id_nombre_jour;
    private String id_nom_de_client;
    private  String id_telephone;


    public Sejour(int id_sejour, String id_nom_de_client, Date date_entre, int id_nombre_jour, String id_numeri_chambre, String id_telephone){
        this.id_sejour = id_sejour;
        this.id_nom_de_client = id_nom_de_client;
        this.date_entre = date_entre;
        this.id_nombre_jour = id_nombre_jour;
        this.id_numeri_chambre = id_numeri_chambre;
        this.id_telephone = id_telephone;
    }



    public int getId_sejour() {
        return id_sejour;
    }

    public void setId_sejour(int id_sejour) {
        this.id_sejour = id_sejour;
    }

    public String getId_nom_de_client() {
        return id_nom_de_client;
    }

    public void setId_nom_de_client(String id_nom_de_client) {
        this.id_nom_de_client = id_nom_de_client;
    }

    public Date getDate_entre() {
        return date_entre;
    }

    public void setDate_entre(Date date_entre) {
        this.date_entre = date_entre;
    }

    public int getId_nombre_jour() {

        return id_nombre_jour;
    }

    public void setId_nombre_jour(int id_nombre_jour) {
        this.id_nombre_jour = id_nombre_jour;
    }

    public String getId_numeri_chambre() {
        return id_numeri_chambre;
    }

    public void setId_numeri_chambre(String id_numeri_chambre) {
        this.id_numeri_chambre = id_numeri_chambre;
    }

    public String getId_telephone() {
        return id_telephone;
    }

    public void setId_telephone(String id_telephone) {
        this.id_telephone = id_telephone;
    }
}

