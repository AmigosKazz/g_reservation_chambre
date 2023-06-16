package kaznarah.reservation_chambre_hotel.models;


public class Chambre {
    private String numero_chambre;
    private String Designation;
    private String Type;
    private int PrixUnitaire;

    public void chambre(){

    }

    public Chambre(String numero_chambre, String designation, String type, int prixUnitaire) {
        this.numero_chambre = numero_chambre;
        this.Designation = designation;
        this.Type = type;
        this.PrixUnitaire = prixUnitaire;
    }

    public String getNumero_chambre() {
        return numero_chambre;
    }

    public void setNumero_chambre(String numero_chambre) {
        this.numero_chambre = numero_chambre;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getPrixUnitaire() {
        return PrixUnitaire;
    }

    public void setPrixUnitaire(int prixUnitaire) {
        PrixUnitaire = prixUnitaire;
    }
}
