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
import kaznarah.reservation_chambre_hotel.models.Chambre;
import kaznarah.reservation_chambre_hotel.utils.ConnexionDB;
import kaznarah.reservation_chambre_hotel.utils.MessageBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;


public class ChambreController implements Initializable {

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btn_ajouter;

    @FXML
    private Button btn_annulé;

    @FXML
    private Button btn_modifer;

    @FXML
    private TextField d_numero_chambre;

    @FXML
    private TextField designation;

    @FXML
    private TextField prix_unitaire;

    @FXML
    private TextField rechercher;

    @FXML
    private TableColumn<Chambre, String> col_designation;

    @FXML
    private TableColumn<Chambre, String> col_num_chambre;

    @FXML
    private TableColumn<Chambre, Integer> col_pu;

    @FXML
    private TableColumn<Chambre, String> col_type;

    @FXML
    private TableView<Chambre> table_chambre;

    @FXML
    private TextField type;

    @FXML
    private Text text_nouveau__chambre;

    // Connexion BDD
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    //pour lister les donnees dans le table
    ObservableList<Chambre> data = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.toggleButtonsModifierSupprimer(false, "NOUVEAU CHAMBRE");
        this.setNumChambre();
        this.listeChambre();
    }

    public void toggleButtonsModifierSupprimer(boolean arg, String text){
        btn_modifer.setVisible(arg);
        btnSupprimer.setVisible(arg);
        text_nouveau__chambre.setText(text);
    }
    @FXML
    void getTableChambre(MouseEvent event) {
        // lorsqu'on clique sur une ligne de la table, cette fonction envoie la valeur en formulaire
        this.toggleButtonsModifierSupprimer(true, "MODIFIER LA TABLE");

        Chambre chambre = table_chambre.getSelectionModel().getSelectedItem();
        d_numero_chambre.setText(chambre.getNumero_chambre());
        designation.setText(chambre.getDesignation());
        type.setText(chambre.getType());
        prix_unitaire.setText(chambre.getPrixUnitaire() + "");// convertir en chaine de caratere
    }

    @FXML
    void onBtnAjouterClicked(ActionEvent event) {
        connection = ConnexionDB.mydb();
        // Recuperer les donnees de la formulaire
        int prix = Integer.parseInt(prix_unitaire.getText());
        String numChambre = d_numero_chambre.getText();
        String design = designation.getText();
        String typeChambre = type.getText();

        // Verifier si les champs obligatoire ne sont pas vide
        if(prix < 0 || numChambre.isEmpty() || design.isEmpty() || typeChambre.isEmpty()){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veuillez remplir les champs vide", "Nouveau Chambre");
        } else {
            String sql = "INSERT INTO chambre(numchambr, design, typ, prixunit) VALUES (?, ?, ?, ?);";

            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, numChambre);
                preparedStatement.setString(2, design);
                preparedStatement.setString(3, typeChambre);
                preparedStatement.setInt(4, prix);

                preparedStatement.execute();
                MessageBox.showInfo(Alert.AlertType.INFORMATION, "Ajout Avec Succès", "Nouveau Chambre");

                this.listeChambre();
                this.clearData();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onBtnAnnuléClicked(ActionEvent event) {
        this.clearData();
    }

    @FXML
    void onBtnModifierClicked(ActionEvent event) {
        connection = ConnexionDB.mydb();
        // Recuperer les donnees de la formulaire
        int prix = Integer.parseInt(prix_unitaire.getText());
        String numChambre = d_numero_chambre.getText();
        String design = designation.getText();
        String typeChambre = type.getText();

        // Verifier si les champs obligatoire ne sont pas vide
        if(prix < 0 || numChambre.isEmpty() || design.isEmpty() || typeChambre.isEmpty()){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veuillez remplir les champs vide", "Nouveau Chambre");
        } else {
            String sql = "UPDATE chambre SET design = ?, typ = ?, prixunit = ? WHERE numchambr = ?;";

            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, design);
                preparedStatement.setString(2, typeChambre);
                preparedStatement.setInt(3, prix);
                preparedStatement.setString(4, numChambre);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation!");

                if(alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.execute();
                    MessageBox.showInfo(Alert.AlertType.INFORMATION, "Modification Succes", "Modification Chambre");
                    this.listeChambre();
                    this.clearData();
                    this.toggleButtonsModifierSupprimer(false, "NOUVEAU CHAMBRE");
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onBtnSupprimerClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false, "NOUVEAU CHAMBRE");

        // Recuperer ligne selectionnée
        Chambre chambre = table_chambre.getSelectionModel().getSelectedItem();
        String sql = "DELETE FROM chambre WHERE numchambr = ?;";

        if(chambre == null){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veuillez selectionner une ligne!", "SUPPRESSION");
        } else {
            connection = ConnexionDB.mydb();

            try{
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, chambre.getNumero_chambre());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation!");

                if(alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.executeUpdate();
                    this.listeChambre();
                    this.clearData();
                    alert.setContentText("Suppression avec Succès");
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onSearchText(KeyEvent event) {
        String recherche = rechercher.getText();
        if(recherche.isEmpty()) this.listeChambre();
        else {
            data.clear();

            connection = ConnexionDB.mydb();
            String sql = "SELECT * FROM chambre;";
            try{
                preparedStatement = connection.prepareStatement(sql);
                resultSet = preparedStatement.executeQuery();

                List<Chambre> chambres = new ArrayList<>();
                while (resultSet.next()){
                    chambres.add(new Chambre(resultSet.getString("numchambr"),
                            resultSet.getString("design"), resultSet.getString("typ"), resultSet.getInt("prixunit")));
                }

                for (Chambre chambre : chambres) {
                    if (
                            chambre.getNumero_chambre().toLowerCase().contains(recherche.toLowerCase()) ||
                                    chambre.getDesignation().toLowerCase().contains(recherche.toLowerCase()) ||
                                    chambre.getType().toLowerCase().contains(recherche.toLowerCase())
                    ) {
                        data.add(chambre);
                    }
                }

                table_chambre.setItems(data);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //Pour effacer les donnée dans la table
    public void clearData(){
        this.setNumChambre();
        designation.setText("");
        type.setText("");
        prix_unitaire.setText("");
    }

    //Pour avoir un numero de chambre en random:
    public void setNumChambre(){
        Random rand = new Random();
        int nb = rand.nextInt(900) + 100;
        d_numero_chambre.setText("CHAMBRE " + nb);
    }

    //
    public void listeChambre(){
        this.setNumChambre();
        data.clear();

        connection = ConnexionDB.mydb();

        String sql = "SELECT * FROM chambre;";

        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            //Parcourir le table
            while (resultSet.next()){
                String numChambre = resultSet.getString("numchambr");
                String design = resultSet.getString("design");
                String type = resultSet.getString("typ");
                int pu = resultSet.getInt("prixunit");

                data.add(new Chambre(numChambre, design, type, pu));

            }

            // Inserer valeurs dans la colonne
            col_num_chambre.setCellValueFactory(new PropertyValueFactory<Chambre, String>("numero_chambre"));
            col_designation.setCellValueFactory(new PropertyValueFactory<Chambre, String>("Designation"));
            col_type.setCellValueFactory(new PropertyValueFactory<Chambre, String>("Type"));
            col_pu.setCellValueFactory(new PropertyValueFactory<Chambre, Integer>("PrixUnitaire"));

            // Inserer dans table
            table_chambre.setItems(data);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
