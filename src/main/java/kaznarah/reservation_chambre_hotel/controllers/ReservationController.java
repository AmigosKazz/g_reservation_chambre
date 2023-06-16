package kaznarah.reservation_chambre_hotel.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import kaznarah.reservation_chambre_hotel.models.Reservation;
import kaznarah.reservation_chambre_hotel.utils.ConnexionDB;
import kaznarah.reservation_chambre_hotel.utils.MessageBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {
    @FXML
    private TableColumn<?, ?> _entrée;

    @FXML
    private TextField addr_mail;

    @FXML
    private Button btn_ajouter;

    @FXML
    private TableColumn<Reservation, Date> col_date_entree;

    @FXML
    private Button btn_modifier;

    @FXML
    private Button btn_supprimer;

    @FXML
    private TableColumn<Reservation, Date> col_date;

    @FXML
    private TableColumn<Reservation, Integer> col_id;


    @FXML
    private Text text_reservation;

    @FXML
    private TableColumn<Reservation, String> col_mail;

    @FXML
    private TableColumn<Reservation, String> col_nom;

    @FXML
    private TableColumn<Reservation, Integer> col_nombre_de_jour;

    @FXML
    private TableColumn<Reservation, String> col_num_chambre;

    @FXML
    private DatePicker date_entre;

    @FXML
    private DatePicker date_reservation;

    @FXML
    private TextField id_reservation;

    @FXML
    private TextField nbre_jour;

    @FXML
    private ComboBox<String> num_chambre;

    @FXML
    private TextField rechercher;

    @FXML
    private TextField votre_nom;

    @FXML
    private Button btn_annullé;



        // Connexion avec BDD
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

        //pour lister les donnees dans la table
    ObservableList<Reservation> data = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.toogleButtonsModifierSupprimer(false, "NOUVELLE RESERVATION");
        this.listeReserver();
        this.genererId();
    }
    public void toogleButtonsModifierSupprimer(boolean arg1, String title){
        btn_modifier.setVisible(arg1);
        btn_supprimer.setVisible(arg1);
        text_reservation.setText(title);
    }

    @FXML
    void onBtnAjouterClicked(ActionEvent event) {
        connection = ConnexionDB.mydb(); //connexion DB

        // recuperation des donnees de la formulaire
        int id_reser = Integer.parseInt(id_reservation.getText());
        String nom = votre_nom.getText();
        String addMail = addr_mail.getText();
        //String numChambre = num_chambre.getValue();
        String nbrJour = nbre_jour.getText();
        Date dateReservation = Date.valueOf(date_reservation.getValue());
        Date dateEntre = Date.valueOf(date_entre.getValue());

        //verification si les champs sont vide
        if (id_reser < 0 || nom.isEmpty() || addMail.isEmpty()){
            MessageBox.showInfo(Alert.AlertType.ERROR,"Veuillez remplir les champs vide!","Nouveau reservation");
        } else {
            String sql = "INSERT INTO reserver(idreserv, numchambr, datereserv, dateentree, nbrjour, nomclient, mail) VALUES(?, ?, ?, ?, ?, ?, ?)";

            try{
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, id_reser);
                preparedStatement.setString(2, nom);
                preparedStatement.setDate(3, dateReservation);
                preparedStatement.setDate(4, dateEntre);
                preparedStatement.setInt(5, Integer.parseInt(nbrJour));
                preparedStatement.setString(6, nom);
                preparedStatement.setString(7, addMail);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if(alert.showAndWait().get()==ButtonType.OK){
                    preparedStatement.execute();
                    MessageBox.showInfo(Alert.AlertType.INFORMATION,"Modification avec Succes","Modificatiion");

                    this.listeReserver();
                    this.clearData();
                }



            } catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    @FXML
    void getSelectRow(MouseEvent event) {
        this.toogleButtonsModifierSupprimer(true,"MODIFICATION RESERVATION");

    }

    @FXML
    void onBtnModifierClicked(ActionEvent event) {
        this.toogleButtonsModifierSupprimer(false, "NOUVEAU RESERVATION");
    }

    @FXML
    void onBtnSupprimerClicked(ActionEvent event) {
        this.toogleButtonsModifierSupprimer(false, "NOUVEAU RESERVATION");
    }
    @FXML
    void onSearchText(KeyEvent event) {

    }
    @FXML
    void onBtnAnnulerClicked(ActionEvent event) {
        this.toogleButtonsModifierSupprimer(false, "NOUVEAU RESERVATION");
    }

    public void listeReserver(){
        this.genererId();
        data.clear();

        connection = ConnexionDB.mydb();
        String sql = "SELECT * FROM reserver;";

        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet =preparedStatement.executeQuery();

            // Parcourir le table
            while (resultSet.next()){
                String iReserv = resultSet.getString("idreserv");
                String numChambre = resultSet.getString("numchambr");
                Date dateReserv = resultSet.getDate("datereserv");
                Date dateEntree = resultSet.getDate("dateentree");
                Integer nbrJour = resultSet.getInt("nbrjour");
                String nomClient = resultSet.getString("nomclient");
                String mail = resultSet.getString("mail");

                data.add(new Reservation(Integer.parseInt(iReserv), nomClient, mail, numChambre, dateReserv, dateEntree, nbrJour));

            //inserer une valeur dans la colonne
                col_id.setCellValueFactory(new PropertyValueFactory<Reservation, Integer>("idResev"));//class au model
                col_nom.setCellValueFactory(new PropertyValueFactory<Reservation, String>("nomclient"));
                col_mail.setCellValueFactory(new PropertyValueFactory<Reservation, String>("mail"));
                col_num_chambre.setCellValueFactory(new PropertyValueFactory<Reservation, String>("numChambre"));
                col_date.setCellValueFactory(new PropertyValueFactory<Reservation, Date>("dateReser"));
                col_date_entree.setCellValueFactory(new PropertyValueFactory<Reservation, Date>("dateEntree"));
                col_nombre_de_jour.setCellValueFactory(new PropertyValueFactory<Reservation, Integer>("nbrJour"));
                col_nom.setCellValueFactory(new PropertyValueFactory<Reservation, String>("nomClient"));
                col_mail.setCellValueFactory(new PropertyValueFactory<Reservation,String>("mail"));
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void genererId(){
        Integer nextID = this.getLastID() +1;
        id_reservation.setText(String.valueOf(nextID));
        num_chambre.getItems().clear();

        connection = ConnexionDB.mydb();
        String sql = "SELECT numchambr FROM chambre;";
        List<String> list = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet =preparedStatement.executeQuery();

            while (resultSet.next()){
                String numChambre =resultSet.getString("numchambr");
                list.add(numChambre);
            }
            num_chambre.getItems().addAll(list);

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public Integer getLastID(){
        String sql = "SELECT idreserv FROM reserver ORDER BY idreserv DESC LIMIT 1;";
        connection =ConnexionDB.mydb();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            Integer lastID = 0;

            while (resultSet.next()){
                lastID =resultSet.getInt("idreserv");
            }

            return lastID;

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    public void clearData(){
        votre_nom.setText("");
        addr_mail.setText("");
        date_reservation.setValue(LocalDate.now());
        date_entre.setValue(LocalDate.now());
        nbre_jour.setText("");
    }


}
