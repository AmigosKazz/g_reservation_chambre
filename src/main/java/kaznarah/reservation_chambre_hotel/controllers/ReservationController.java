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

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {

    private static final String SMTP_HOST = "smtp.gmail.com"; // Remplacez par l'hôte SMTP de votre fournisseur de messagerie
    private static final int SMTP_PORT = 587; // Remplacez par le port SMTP de votre fournisseur de messagerie
    private static final String USERNAME = "kaznarahandrinarivo@gmail.com"; // Remplacez par votre adresse e-mail
    private static final String PASSWORD = "kaznarah1203"; // Remplacez par votre mot de passe



    @FXML
    private TextField id_reservation;
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
    private TableView<Reservation> table_reserer;

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
    private TextField nbre_jour;

    @FXML
    private ComboBox<String> num_chambre;

    @FXML
    private TextField rechercher;

    @FXML
    private TextField votre_nom;

    @FXML
    private Button btn_annullé;


    @FXML
    private Button envoyerMail;



        // Connexion avec BDD
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

        //pour lister les donnees dans la table
    ObservableList<Reservation> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.toogleButtonsModifierSupprimer(false, "NOUVELLE RESERVATION");
        this.visibleEnvoyerMail(false);
        this.listeReserver();
        this.genererId();
    }


    //affichage des boutons de modification et de suppression
    public void toogleButtonsModifierSupprimer(boolean arg1, String title){
        btn_modifier.setVisible(arg1);
        btn_supprimer.setVisible(arg1);
        text_reservation.setText(title);
        envoyerMail.setVisible(arg1);

    }

    @FXML
    void onBtnAjouterClicked(ActionEvent event) {
        connection = ConnexionDB.mydb(); //connexion DB

        // recuperation des valeurs à partir des champs de saisie d'une interface(manarak n lign table)
        int id_reser = Integer.parseInt(id_reservation.getText());
        String numChambre = num_chambre.getValue();  //generer'na auto am alalan table ito
        Date dateReservation = Date.valueOf(date_reservation.getValue());
        Date dateEntre = Date.valueOf(date_entre.getValue());
        String nbrJour = nbre_jour.getText();
        String nom = votre_nom.getText();
        String addMail = addr_mail.getText();

        //verification si les champs sont vide
        if (id_reser < 0 || nom.isEmpty() || addMail.isEmpty()){
            MessageBox.showInfo(Alert.AlertType.ERROR,"Veuillez remplir les champs vide!","Nouveau reservation");
        }else {
            //Inserer la nouvelle reservation
            String insertQuery = "SELECT COUNT(*) FROM reserver WHERE numchambr = ? AND dateentree = ?";
            try {
                preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1,numChambre);
                preparedStatement.setDate(2,dateEntre);
                ResultSet resultSet1 = preparedStatement.executeQuery();
                resultSet1.next();
                int count = resultSet1.getInt(1);
                if (count>0){
                    MessageBox.showInfo(Alert.AlertType.ERROR,"La chambre est déjà réservée pour cette date !","Nouveau reservation");

                } else {
                    //Manarak ny rang an column am Base de donnée
                    String sql = "INSERT INTO reserver(idreserv, numchambr, datereserv, dateentree, nbrjour, nomclient, mail) VALUES(?, ?, ?, ?, ?, ?, ?);";

                    try{
                        preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setInt(1, id_reser);
                        preparedStatement.setString(2, numChambre);
                        preparedStatement.setDate(3, dateReservation);
                        preparedStatement.setDate(4, dateEntre);
                        preparedStatement.setInt(5, Integer.parseInt(nbrJour));
                        preparedStatement.setString(6, nom);
                        preparedStatement.setString(7, addMail);

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setContentText("Confirmation");

                        if(alert.showAndWait().get()==ButtonType.OK){
                            preparedStatement.execute();
                            MessageBox.showInfo(Alert.AlertType.INFORMATION,"Ajout avec succes!","Ajout");

                            this.listeReserver();
                            this.clearData();
                        }



                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        try{
            //inserer la nouvelle reservation dans la base de donnée(comme avant)

            //Envoie de l'email au client
            String toEmail = addMail;
            String subject = "confirmation de réservation";
            String message = "bonjour "+nom+",\n\n"+
                    "Nous sommes heureux de vous  confirmer votre résérvation .\n"+
                    "Chambre occupée : "+numChambre+"\n"+
                    "Date de réservation : du "+dateReservation+" au "+dateEntre+"\n"+
                    "Nombre de jours : "+nbrJour+"\n\n"+
                    "Merci pour votre reservation.Nous avons hâte  de vous acceuillir !\n\n"+
                    "Cordialement,\n"+
                    "Hotel le Glacier";

            sendEmail(toEmail,subject,message);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    void getSelectRow(MouseEvent event) {
        this.toogleButtonsModifierSupprimer(true,"MODIFICATION RESERVATION");
        this.visibleEnvoyerMail(    false);

        // Récupérer la table sélectionnée
        Reservation reservation =table_reserer.getSelectionModel().getSelectedItem();
        id_reservation.setText(reservation.getId_reservation()+"");
        votre_nom.setText(reservation.getVotre_nom());
        addr_mail.setText(reservation.getVotre_mail());
        nbre_jour.setText(String.valueOf(reservation.getNbre_jour()));
        num_chambre.setValue(reservation.getNum_chambre());
        date_reservation.setValue(reservation.getDate().toLocalDate());
        date_entre.setValue(reservation.getDate_entre().toLocalDate());
    }

    @FXML
    void onBtnModifierClicked(ActionEvent event) {

        connection = ConnexionDB.mydb();

        // reccuperer les donner de formulaire
        int idReserv = Integer.parseInt(id_reservation.getText());
        String nomClient = votre_nom.getText();
        String mail = addr_mail.getText();
        String numChambr = num_chambre.getValue();
        Date dateReserv = Date.valueOf(date_reservation.getValue());
        Date dateEntre = Date.valueOf(date_entre.getValue());
        int nbrJour = Integer.parseInt(nbre_jour.getText());

        //Verification si les champ vide ne sont pas vide
        if(idReserv < 0 || nomClient.isEmpty() || mail.isEmpty() || numChambr.isEmpty() || nbrJour < 0){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veillez verifier le champ vide","Nouveau reservaion");
        } else{
            String sql = "UPDATE reserver SET numchambr=?,datereserv=?,dateentree=?,nbrjour=?,nomclient=?,mail=? WHERE idreserv=?;";

            try{
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,numChambr);
                preparedStatement.setDate(2,dateReserv);
                preparedStatement.setDate(3,dateEntre);
                preparedStatement.setInt(4,nbrJour);
                preparedStatement.setString(5,nomClient);
                preparedStatement.setString(6,mail);
                preparedStatement.setInt(7,idReserv);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if(alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.execute();
                    MessageBox.showInfo(Alert.AlertType.INFORMATION,"Modification Succes","Modification Reservation");
                    this.listeReserver();
                    this.clearData();
                    this.toogleButtonsModifierSupprimer(false, "NOUVEAU RESERVATION");
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onBtnSupprimerClicked(ActionEvent event) {
        this.toogleButtonsModifierSupprimer(false, "NOUVEAU RESERVATION");
        //recuperer la liste selectionner
        Reservation reservation =table_reserer.getSelectionModel().getSelectedItem();
        String sql = "DELETE FROM reserver WHERE idreserv = ?;";

        if(reservation == null){
            MessageBox.showInfo(Alert.AlertType.ERROR,"Veuillez selectionner une ligne!","SUPPRESSION");
        } else {
            connection = ConnexionDB.mydb();

            try {
                assert connection != null;
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,reservation.getId_reservation());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if(alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.executeUpdate();
                    this.listeReserver();
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
        String recherche =rechercher.getText();
        if(recherche.isEmpty()) this.listeReserver();
        else {
            data.clear();
            connection = ConnexionDB.mydb();
            String sql = "SELECT * FROM reserver;";

            try{
                assert connection != null;
                preparedStatement = connection.prepareStatement(sql);
                resultSet =preparedStatement.executeQuery();

                List<Reservation>reservations =new ArrayList<>();
                while (resultSet.next()){
                    reservations.add(new Reservation(resultSet.getInt("idreserv"),
                            resultSet.getString("numchambr"), resultSet.getDate("datereserv"),resultSet.getDate("dateentree"),
                            resultSet.getInt("nbrjour"),resultSet.getString("nomclient"),resultSet.getString("mail")));
                }

                for (Reservation reservation : reservations) {
                    if (reservation != null) {
                        if (
                                reservation.getNum_chambre() != null && reservation.getNum_chambre().toLowerCase().contains(recherche.toLowerCase()) ||
                                        reservation.getVotre_nom() != null && reservation.getVotre_nom().toLowerCase().contains(recherche.toLowerCase()) ||
                                        reservation.getVotre_mail() != null && reservation.getVotre_mail().toLowerCase().contains(recherche.toLowerCase())
                        ) {
                            data.add(reservation);
                        }
                    }
                }

                table_reserer.setItems(data);


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @FXML
    void onBtnAnnulerClicked(ActionEvent event) {
        this.toogleButtonsModifierSupprimer(false, "NOUVEAU RESERVATION");
        this.clearData();

    }

    public void listeReserver(){
        this.genererId();
        data.clear();

        connection = ConnexionDB.mydb();
        String sql = "SELECT * FROM reserver;";

        try{
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            resultSet =preparedStatement.executeQuery();

            // Parcourir le table
                while (resultSet.next()){
                    int idReservation = resultSet.getInt("idreserv");
                    String numChambre = resultSet.getString("numchambr");
                    Date date = resultSet.getDate("datereserv");
                    Date dateEntree = resultSet.getDate("dateentree");
                    int nbrJour = resultSet.getInt("nbrjour");
                    String nomClient = resultSet.getString("nomclient");
                    String mail = resultSet.getString("mail");

                    data.add(new Reservation(idReservation,numChambre,date,dateEntree,nbrJour,nomClient,mail));
                }
            //inserer une valeur dans la colonne
            col_id.setCellValueFactory(new PropertyValueFactory<Reservation, Integer>("id_reservation"));//class au model
            col_num_chambre.setCellValueFactory(new PropertyValueFactory<Reservation, String>("num_chambre"));
            col_date.setCellValueFactory(new PropertyValueFactory<Reservation, Date>("date"));
            col_date_entree.setCellValueFactory(new PropertyValueFactory<Reservation, Date>("date_entre"));
            col_nombre_de_jour.setCellValueFactory(new PropertyValueFactory<Reservation, Integer>("nbre_jour"));
            col_nom.setCellValueFactory(new PropertyValueFactory<Reservation, String>("votre_nom"));
            col_mail.setCellValueFactory(new PropertyValueFactory<Reservation, String>("votre_mail"));

            table_reserer.setItems(data);

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
            assert connection != null;
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
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            int lastID = 0;

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
        nbre_jour.setText("");
    }

    public void visibleEnvoyerMail(boolean arg){
        envoyerMail.setVisible(arg);
    }

    private void sendEmail(String toEmail, String subject, String message){
        String fromEmail = "kaznarahandrinarivo@gmail.com";
        String password = "ujjsxmjnqzzzevmo";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");//Port SMTP

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail,password);
            }
        });

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(fromEmail));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            System.out.println("Email envoyé avec succes !");

        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
