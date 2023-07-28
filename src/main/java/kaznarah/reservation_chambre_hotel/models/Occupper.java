package kaznarah.reservation_chambre_hotel.models;


public class Occupper {
    private int id_occuper;
    private int id_reservation;

    public Occupper(int id_occuper, int id_reservation){
        this.id_occuper = id_occuper;
        this.id_reservation = id_reservation;
    }

    public int getId_occuper() {
        return id_occuper;
    }

    public void setId_occuper(int id_occuper) {
        this.id_occuper = id_occuper;
    }

    public int getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(int id_reservation) {
        this.id_reservation = id_reservation;
    }
}
