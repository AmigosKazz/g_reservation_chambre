package kaznarah.reservation_chambre_hotel.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import kaznarah.reservation_chambre_hotel.models.Occupper;
import kaznarah.reservation_chambre_hotel.utils.ConnexionDB;
import kaznarah.reservation_chambre_hotel.utils.MessageBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class OccuperController implements Initializable {
    @FXML
    private Button btn_ajouter;

    @FXML
    private Button btn_annullé;

    @FXML
    private Button btn_modifier;

    @FXML
    private Button btn_supprimmer;

    @FXML
    private TableColumn<?, ?> col_id_occupper;

    @FXML
    private TableColumn<?, ?> col_id_reservation;

    @FXML
    private TextField id_Occupper;

    @FXML
    private TextField id_reservation;

    @FXML
    private TextField rechercher;

    @FXML
    private TableView<?> tableOccuper;

    //Connexion BDD
Connection connection;
PreparedStatement preparedStatement;
ResultSet resultSet;

//pour lister les donnees de la table
ObservableList<Occupper> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.toggleButtonsModifierSupprimer(false);
    }

    public void toggleButtonsModifierSupprimer(boolean arg){
        btn_modifier.setVisible(arg);
        btn_supprimmer.setVisible(arg);
    }

    @FXML
    void getTableOccuper(MouseEvent event) {
        this.toggleButtonsModifierSupprimer(true);
    }

    @FXML
    void onBtnAjouterClicked(ActionEvent event) {
        connection = ConnexionDB.mydb();

        //Recuperer les donnees de la formulaire
        String idOccup = id_Occupper.getText();
        String idReserv = id_reservation.getText();

        // verification si le champ est vide
        if(idOccup.isEmpty() || idReserv.isEmpty()){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veuillez remplir les champs vide","Occupation");
        } else {
            String sql = "INSERT INTO occuper(idoccup, idreserv) VALUES(?, ?);";

            try{
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, Integer.parseInt(idOccup));
                preparedStatement.setInt(2, Integer.parseInt(idReserv));

                preparedStatement.execute();
                MessageBox.showInfo(Alert.AlertType.CONFIRMATION,"Ajout avec succes", "Occuper");

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onBtnAnnulerClicked(ActionEvent event) {

    }

    @FXML
    void onBtnModifierClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false);
    }

    @FXML
    void onBtnSupprimerClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false);
    }

    @FXML
    void onSearchText(KeyEvent event) {

    }

}
