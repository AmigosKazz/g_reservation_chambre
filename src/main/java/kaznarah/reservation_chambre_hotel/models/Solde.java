package kaznarah.reservation_chambre_hotel.models;

public class Solde {
    private int id;
    private int soldeActuel;

    public Solde() {

    }

    public Solde(int id, int soldeActuel) {
        this.id = id;
        this.soldeActuel = soldeActuel;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getSoldeActuel() {

        return soldeActuel;
    }

    public void setSoldeActuel(int soldeActuel) {
        this.soldeActuel = soldeActuel;
    }

}
