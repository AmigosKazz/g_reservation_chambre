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
import kaznarah.reservation_chambre_hotel.models.Occupper;
import kaznarah.reservation_chambre_hotel.utils.ConnexionDB;
import kaznarah.reservation_chambre_hotel.utils.ExportDoc;
import kaznarah.reservation_chambre_hotel.utils.MessageBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.io.IOException;
import java.net.HttpURLConnection;

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
    private TableColumn<Occupper, Integer> col_id_occupper;

    @FXML
    private TableColumn<Occupper, Integer> col_id_reservation;

    @FXML
    private TextField id_Occupper;


    @FXML
    private ComboBox<Integer> id_reservation;

    @FXML
    private TextField rechercher;

    @FXML
    private TableView<Occupper> tableOccup;

    @FXML
    private Button exporter_pdf;

    @FXML
    private Text text;


    //Connexion BDD
Connection connection;
PreparedStatement preparedStatement;
ResultSet resultSet;

//pour lister les donnees de la table
ObservableList<Occupper> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.toggleButtonsModifierSupprimer(false,"NOUVEAU OCCUPER");
        this.visibleExportePdf(false);
        this.listeOccuper();
        this.genererID();
    }

    public void toggleButtonsModifierSupprimer(boolean arg, String Text){
        btn_modifier.setVisible(arg);
        btn_supprimmer.setVisible(arg);
        text.setText(Text);

    }

    @FXML
    void getTableOccuper(MouseEvent event) {
        this.toggleButtonsModifierSupprimer(true,"MODIFIER OCCUPER");
        this.visibleExportePdf(false);

        //Recuperer la table selectionner
        Occupper occupper = (Occupper) tableOccup.getSelectionModel().getSelectedItem();
        if(occupper != null){
            id_Occupper.setText(String.valueOf(occupper.getId_occuper()));
            id_reservation.setValue(occupper.getId_reservation());
        }
    }

    @FXML
    void onBtnAjouterClicked(ActionEvent event) {
        connection = ConnexionDB.mydb();

        //Recuperer les donnees de la formulaire
        int idOccup =Integer.parseInt(id_Occupper.getText());
        int idReserv = Integer.parseInt(String.valueOf(id_reservation.getValue()));

        // verification si le champ est vide
        if(idOccup<0 || idReserv<0){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veuillez remplir les champs vide","Occupation");
        } else {
            String sql = "INSERT INTO occuper(idoccup, idreserv) VALUES(?, ?);";

            try{
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, idOccup);
                preparedStatement.setInt(2, idReserv);

                preparedStatement.execute();
                MessageBox.showInfo(Alert.AlertType.CONFIRMATION,"Ajout avec succes", "Occuper");

                this.listeOccuper();
                this.clearData();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onBtnAnnulerClicked(ActionEvent event) {
        this.clearData();
    }

    @FXML
    void onBtnModifierClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false,"NOUVEAU OCCUPER");

        connection = ConnexionDB.mydb();
        //recuoerer les valeur dans la farmulaires
        int id_occup = Integer.parseInt(id_Occupper.getText());
        int id_reserv = id_reservation.getValue();

        //verfication si les champs ne sont pas vide
        if(id_occup<0 || id_reserv<0 ){
            MessageBox.showInfo(Alert.AlertType.ERROR,"Veillez verifier le champ vide","Nouveau Sejour");
        } else {
            String sql = "UPDATE occuper SET idreserv=? WHERE idoccup=?;";

            try{
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,id_reserv);
                preparedStatement.setInt(2,id_occup);

                Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if(alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.execute();
                    MessageBox.showInfo(Alert.AlertType.INFORMATION,"modification avec succes","Modification occuper");
                    this.listeOccuper();
                    this.clearData();
                    this.toggleButtonsModifierSupprimer(false,"NOUVEAU OCCUPER");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onBtnSupprimerClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false,"NOUVEAU OCCUPER");
        Occupper occupper =tableOccup.getSelectionModel().getSelectedItem();
        String sql = "DELETE FROM occuper WHERE idoccup=?;";

        //verification si le champ est vide
        if (occupper == null){
            MessageBox.showInfo(Alert.AlertType.ERROR,"Veuillez selectionner une ligne","SUPPRESSION");
        } else {
            connection = ConnexionDB.mydb();

            try {
                assert connection != null;
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,occupper.getId_occuper());

                Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if(alert.showAndWait().get()==ButtonType.OK){
                    preparedStatement.execute();
                    this.listeOccuper();
                    this.clearData();
                    alert.setContentText("Suppremer avec succes");
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onSearchText(KeyEvent event) {

    }

    public void listeOccuper(){
        this.genererID();
        data.clear();

        // connection BDD
        connection = ConnexionDB.mydb();
        String sql = "SELECT * FROM occuper;";

        try{
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            // Parcourier la table
            while (resultSet.next()){
                int idOccup = resultSet.getInt("idoccup");
                int idReserv = resultSet.getInt("idreserv");

                data.add(new Occupper(idOccup,idReserv));

                //inserer la valeur dans la colonne
                col_id_occupper.setCellValueFactory(new PropertyValueFactory<Occupper,Integer>("id_occuper"));
                col_id_reservation.setCellValueFactory(new PropertyValueFactory<Occupper,Integer>("id_reservation"));
            }

            tableOccup.setItems(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void genererID(){
        Integer nextID =this.getLastID() + 1;
        id_Occupper.setText(String.valueOf(nextID));
        id_reservation.getItems().clear();

        connection = ConnexionDB.mydb();
        String sql = "SELECT idreserv FROM reserver;";

        List<Integer> list = new ArrayList<>();

        try{
            assert connection!=null;
            preparedStatement =connection.prepareStatement(sql);
            resultSet =preparedStatement.executeQuery();

            while (resultSet.next()){
                int idReserv = resultSet.getInt("idreserv");
                list.add(idReserv);
            }
            id_reservation.getItems().addAll(list);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Integer getLastID(){
        String sql = "SELECT idoccup FROM occuper ORDER BY idoccup DESC LIMIT 1;";
        connection = ConnexionDB.mydb();

        try{
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            int lastID = 0;

            while (resultSet.next()){
                lastID= resultSet.getInt("idoccup");
            }

            return lastID;
        } catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public void visibleExportePdf(boolean arg){
        exporter_pdf.setVisible(arg);
    }


    @FXML
    void onBtnExporterPdfClicked(ActionEvent event) {

    }

    public void clearData(){
        id_reservation.setValue(null);
    }

}
