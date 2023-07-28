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
import kaznarah.reservation_chambre_hotel.models.Solde;
import kaznarah.reservation_chambre_hotel.utils.ConnexionDB;
import kaznarah.reservation_chambre_hotel.utils.ExportDoc;
import kaznarah.reservation_chambre_hotel.utils.MessageBox;


import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.List;

public class SoldeController implements Initializable {

    @FXML
    private Button btn_ajouter;

    @FXML
    private Button btn_annuler;

    @FXML
    private Button btn_modifier;

    @FXML
    private Button btn_supprimer;

    @FXML
    private TableColumn<Solde, Integer> col_id;

    @FXML
    private TableColumn<Solde, Integer> col_solde;

    @FXML
    private TextField id_solde;


    @FXML
    private TextField rechercher;

    @FXML
    private TextField solde_actuel;

    @FXML
    private TableView<Solde> table_solde;

    @FXML
    private Text txt_nouveau_solde;

    @FXML
    private Button exporter_pdf;

    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    //pour lister les donnees dans le table
    ObservableList<Solde> data= FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.toggleButtonsModifierSupprimer(false, "NOUVEAU SOLDE");
        this.listeSolde();
        this.genererId();
        this.visibleBtnExporterPdf(false);

    }
    public void toggleButtonsModifierSupprimer(Boolean arg1, String title){
        btn_modifier.setVisible(arg1);
        btn_supprimer.setVisible(arg1);
        txt_nouveau_solde.setText(title);
    }
    @FXML
    void getSelectedRow(MouseEvent event) {

        this.toggleButtonsModifierSupprimer(true, "MODIFICATION SOLDE");
        this.visibleBtnExporterPdf(false);
        Solde solde =table_solde.getSelectionModel().getSelectedItem();
        id_solde.setText(solde.getId() + ""); // convertir en String
        solde_actuel.setText(solde.getSoldeActuel()+"");
    }

    @FXML
    void onBtnAjouterClicked(ActionEvent event) {
        //connexion BDD
        connection = ConnexionDB.mydb();

        //recuperation les donnees de la formulaire
        String soldeID = id_solde.getText();
        String soldeActuel = solde_actuel.getText();

        //verifier si les champs ne sont pas vide
        if(soldeActuel.isEmpty()){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veuillez remplir les champs vide", "Nouveau solde");
        } else {
            String sql = "INSERT INTO solde(id, solde) VALUES(?, ?);";

            try{
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, Integer.parseInt(soldeID));
                preparedStatement.setInt(2, Integer.parseInt(soldeActuel));

                preparedStatement.execute();
                MessageBox.showInfo(Alert.AlertType.INFORMATION,"Ajout avec succes", "Nouveau solde");

                this.listeSolde();
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

        this.toggleButtonsModifierSupprimer(false, "NOUVEAU SOLDE");

        connection = ConnexionDB.mydb();
        //Recuperation les donnée dans la table
        int idSolde = Integer.parseInt(id_solde.getText());
        int soldeActuel = Integer.parseInt(solde_actuel.getText());

        //Verification si les champ vide ne sont pas vide
        if (idSolde < 0 || soldeActuel < 0){
                MessageBox.showInfo(Alert.AlertType.ERROR, "Veillez verifier le champ vide", "Nouveau chambre");
        } else {
            String sql = "UPDATE solde SET solde=? WHERE id =?;";

            try {
                //recuperer values
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, soldeActuel); //manaraka anle filaharan am requet sql io
                preparedStatement.setInt(2, idSolde);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if(alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.execute();
                    MessageBox.showInfo(Alert.AlertType.INFORMATION,"Modification avec Succes", "Modification");
                    this.listeSolde();
                    this.clearData();
                    this.toggleButtonsModifierSupprimer(false, "NOUVEAU SOLDE");

                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @FXML
    void onBtnSupprimerClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false, "NOUVEAU SOLDE");

        //recuperer la liste selectionner
        Solde solde =table_solde.getSelectionModel().getSelectedItem();
        String sql = "DELETE FROM solde WHERE id = ?;";

        if(solde == null){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veuillez selectionner une ligne!","SUPPRESSION");
        } else {
            connection = ConnexionDB.mydb();

            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, solde.getId());

                Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if (alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.executeUpdate();
                    this.listeSolde();
                    this.clearData();
                    alert.setContentText("Supprimer avec succes");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onSearchText(KeyEvent event) {
        String recherche = rechercher.getText();
        if (recherche.isEmpty()) {
            this.listeSolde();
        } else {
            data.clear();
            connection = ConnexionDB.mydb();
            String sql = "SELECT * FROM solde WHERE id=?;";

            try {
                int valeurRechercher = Integer.parseInt(recherche);
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, valeurRechercher);
                resultSet = preparedStatement.executeQuery();

                List<Solde> soldes = new ArrayList<>();
                while (resultSet.next()){
                    soldes.add(new Solde(resultSet.getInt("id"),resultSet.getInt("solde")));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void visibleBtnExporterPdf(boolean arg){
        exporter_pdf.setVisible(arg);
    }
    //exporter en PDF
    @FXML
    void onBtnExporterPdfClicked(ActionEvent event) {
        ExportDoc.exportToPDF(table_solde, ExportDoc.getFilePath("PDF files","*.pdf"));
    }


    //fonction clear data:
    public void clearData(){
        solde_actuel.setText("");
    }

    //
    public void listeSolde(){
        this.genererId();
        data.clear();

        connection = ConnexionDB.mydb();
        String sql = "SELECT * FROM solde;";

        try{
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            //Parcourir le table
            while (resultSet.next()){
                String idSolde = resultSet.getString("id");// colonne @ base de donnee
                String solde_actuel = resultSet.getString("solde");

                data.add(new Solde(Integer.parseInt(idSolde), Integer.parseInt(solde_actuel)));


            // Inserer une valeur dans la colonne
                col_id.setCellValueFactory(new PropertyValueFactory<Solde, Integer>("id"));// class modele
                col_solde.setCellValueFactory(new PropertyValueFactory<Solde, Integer>("soldeActuel"));
            }

            //inserer dans la table
            table_solde.setItems(data);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    // genererID
    public void genererId(){
        Integer nextID = this.getLastID() + 1;
        id_solde.setText(String.valueOf(nextID));
    }

    // Recuperer le dernier id de la BDD
    public int getLastID(){
        String sql = "SELECT id FROM solde ORDER BY id DESC LIMIT 1;";
        connection = ConnexionDB.mydb();

        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            Integer lastID = 0;

            while (resultSet.next()){
                lastID = resultSet.getInt("id");
            }

            return lastID;

        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

}

