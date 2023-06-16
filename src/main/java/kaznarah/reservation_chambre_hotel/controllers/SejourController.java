package kaznarah.reservation_chambre_hotel.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class SejourController implements Initializable {
    @FXML
    private Button btn_ajouter;

    @FXML
    private Button btn_annullé;

    @FXML
    private Button btn_modifier;

    @FXML
    private Button btn_supprimmer;

    @FXML
    private TableColumn<?, ?> col_date_entrer;

    @FXML
    private TableColumn<?, ?> col_id_sejour;

    @FXML
    private TableColumn<?, ?> col_nom;

    @FXML
    private TableColumn<?, ?> col_nombre_de_jour;

    @FXML
    private TableColumn<?, ?> col_numero_chambre;

    @FXML
    private TableColumn<?, ?> col_telephone;

    @FXML
    private TextField id_date_entre;

    @FXML
    private TextField id_nom_de_client;

    @FXML
    private TextField id_nombre_jour;

    @FXML
    private TextField id_numeri_chambre;

    @FXML
    private TextField id_sejour;

    @FXML
    private TextField id_telephone;

    @FXML
    private TextField rechercher;


    @FXML
    private Text textSejour;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleButtonsModifierSupprimer(false, "NOUVEAU SEJOUR");
    }

    public void toggleButtonsModifierSupprimer(boolean arg, String Text){
        btn_modifier.setVisible(arg);
        btn_supprimmer.setVisible(arg);
        textSejour.setText(Text);
    }

    @FXML
    void getTableSejour(MouseEvent event) {
        this.toggleButtonsModifierSupprimer(true, "MODIFICATION TABLE");
    }
    @FXML
    void on(KeyEvent event) {

    }

    @FXML
    void onBtnAjouterrClicked(ActionEvent event) {

    }

    @FXML
    void onBtnAnnuléClicked(ActionEvent event) {

    }

    @FXML
    void onBtnModifierClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false, "NOUVEAU SEJOUR");
    }

    @FXML
    void onBtnSupprimerClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false, "NOUVEAU SEJOUR");
    }

    @FXML
    void onSearchText(KeyEvent event) {

    }

    @FXML
    void t(RotateEvent event) {

    }


}
