package kaznarah.reservation_chambre_hotel.controllers;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kaznarah.reservation_chambre_hotel.MainApp;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Parent fxml;
    private Stage stage;


    @FXML
    private Pane btn_deconnecter;

    @FXML
    private Pane content;

    @FXML
    private Pane menu_accueil;

    @FXML
    private Pane menu_chambre;

    @FXML
    private Pane menu_occuper;

    @FXML
    private Pane menu_reservation;

    @FXML
    private Pane menu_sejour;


    @FXML
    private Pane menu_solde;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.onMenuAccueilClicked(null);
    }

    @FXML
    void onLogoutClickedd(MouseEvent event) {

    }

    //Prender en parametre le nom de la vue FXML que tu veux charger
    public void loadFxmlView(String view){
        try {
            this.fxml = FXMLLoader.load(Objects.requireNonNull(MainApp.class.getResource(view)));
            content.getChildren().removeAll();
            content.getChildren().setAll(this.fxml);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    void onMenuAccueilClicked(MouseEvent event)  {
        this.loadFxmlView("Accueil.fxml");
        this.activePane(menu_accueil);
        Pane[] panes = {menu_sejour, menu_reservation, menu_occuper, menu_chambre, menu_solde };
        this.deactivePane(panes);
    }

    @FXML
    void onMenuChambreClicked(MouseEvent event) {
        this.loadFxmlView("Chambre.fxml");
        this.activePane(menu_chambre);
        Pane[] panes = {menu_sejour, menu_reservation, menu_occuper, menu_accueil, menu_solde};
        this.deactivePane(panes);
    }

    @FXML
    void onMenuOcuperClicked(MouseEvent event) {
        this.loadFxmlView("Occuper.fxml");
        this.activePane(menu_occuper);
        Pane[] panes = {menu_sejour, menu_reservation, menu_chambre , menu_accueil, menu_solde};
        this.deactivePane(panes);
    }

    @FXML
    void onMenuReservationClicked(MouseEvent event) {
        this.loadFxmlView("reservation.fxml");
        this.activePane(menu_reservation);
        Pane[] panes = {menu_sejour, menu_occuper, menu_chambre , menu_accueil, menu_solde};
        this.deactivePane(panes);
    }

    @FXML
    void onMenuSejourClicked(MouseEvent event) {
        this.loadFxmlView("sejour.fxml");
        this.activePane(menu_sejour);
        Pane[] panes = {menu_reservation, menu_occuper, menu_chambre , menu_accueil, menu_solde};
        this.deactivePane(panes);
    }

    @FXML
    void onMenuSoldeClicked(MouseEvent event) {
        this.loadFxmlView("Solde.fxml");
        this.activePane(menu_solde);
        Pane[] panes = {menu_reservation, menu_occuper, menu_chambre , menu_accueil, menu_sejour};
        this.deactivePane(panes);
    }

    void activePane(Pane pane) {

        pane.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
    }
    void deactivePane(Pane[] panes) {
        for (Pane pane2 : panes) {
            pane2.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
        }
    }


}
