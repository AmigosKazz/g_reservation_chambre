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
        import javafx.scene.input.RotateEvent;
        import javafx.scene.text.Text;
        import kaznarah.reservation_chambre_hotel.models.Sejour;
        import kaznarah.reservation_chambre_hotel.utils.ConnexionDB;
        import kaznarah.reservation_chambre_hotel.utils.ExportDoc;
        import kaznarah.reservation_chambre_hotel.utils.MessageBox;
        import org.apache.pdfbox.pdmodel.PDDocument;
        import org.apache.pdfbox.pdmodel.PDPage;
        import org.apache.pdfbox.pdmodel.PDPageContentStream;
        import org.apache.pdfbox.pdmodel.common.PDRectangle;
        import org.apache.pdfbox.pdmodel.font.PDType1CFont;
        import java.io.File;
        import java.io.IOException;

        import java.net.URL;
        import java.sql.Connection;
        import java.sql.Date;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.text.SimpleDateFormat;
        import java.time.LocalDate;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.ResourceBundle;
        import java.time.LocalDate;
        import java.time.ZoneId;
        import org.apache.pdfbox.pdmodel.font.PDType1Font;


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
    private Button export_pdf;

    @FXML
    private TableColumn<Sejour, Date> col_date_entrer;

    @FXML
    private TableColumn<Sejour, Integer> col_id_sejour;

    @FXML
    private TableColumn<Sejour, String> col_nom;

    @FXML
    private TableColumn<Sejour, Integer> col_nombre_de_jour;

    @FXML
    private TableColumn<Sejour, String> col_numero_chambre;

    @FXML
    private TableColumn<Sejour, String> col_telephone;


    @FXML
    private DatePicker date_entre;

    @FXML
    private TextField id_nom_de_client;

    @FXML
    private TextField id_nombre_jour;

    @FXML
    private ComboBox<String> id_numeri_chambre = new ComboBox<>();

    @FXML
    private TextField id_sejour;

    @FXML
    private TextField id_telephone;


    @FXML
    private TextField recherche;


    @FXML
    private Text textSejour;

    @FXML
    private TableView<Sejour> table_sejour;



    // connection BDD
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    // Pour lister les donnees dans la table
    ObservableList<Sejour> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleButtonsModifierSupprimer(false, "NOUVEAU SEJOUR");
        this.visibleExporterPdf(false);
        this.listeSejour();
        this.genererID();
    }

    public void toggleButtonsModifierSupprimer(boolean arg, String Text){
        btn_modifier.setVisible(arg);
        btn_supprimmer.setVisible(arg);
        textSejour.setText(Text);
    }

    @FXML
    void getTableSejour(MouseEvent event) {
        this.toggleButtonsModifierSupprimer(true, "MODIFICATION TABLE");
        this.visibleExporterPdf(true);
        Sejour sejour = table_sejour.getSelectionModel().getSelectedItem();

        if(sejour != null){
            id_sejour.setText(sejour.getId_sejour()+"");
            id_nom_de_client.setText(sejour.getId_nom_de_client());
            date_entre.setValue(sejour.getDate_entre().toLocalDate());
            id_nombre_jour.setText(sejour.getId_nombre_jour()+"");
            id_numeri_chambre.setValue(sejour.getId_numeri_chambre());
            id_telephone.setText(sejour.getId_telephone());
        }
    }
    @FXML
    void on(KeyEvent event) {

    }
    @FXML
    void onBtnAjouterrClicked(ActionEvent event) {
        // Connection
        connection = ConnexionDB.mydb();

        // recuperation les valeur dans la formulaire
        int idSejour = Integer.parseInt(id_sejour.getText());
        String numChambre = id_numeri_chambre.getValue();
        Date dateEntre = Date.valueOf(date_entre.getValue());
        int nombreJour = Integer.parseInt(id_nombre_jour.getText());
        String nomClient = id_nom_de_client.getText();
        String idTel = id_telephone.getText();

        // Verification si le champ est vide
        if(idSejour < 0 || nomClient.isEmpty() || numChambre.isEmpty() || nombreJour < 0 || idTel.isEmpty()){
            MessageBox.showInfo(Alert.AlertType.ERROR, "Veuillez remplir les champs vide!", "Nouveau sejour");
        } else {
            String sql = "INSERT INTO sejourner(idsejour, numchambr, dateentreesejour, nbrjour, nomclient, telephone) VALUES(?, ?, ?, ?, ?, ?);";

            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, idSejour);
                preparedStatement.setString(2, numChambre);
                preparedStatement.setDate(3,dateEntre);
                preparedStatement.setInt(4, nombreJour);
                preparedStatement.setString(5, nomClient);
                preparedStatement.setString(6, idTel);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if(alert.showAndWait().get()==ButtonType.OK){
                    preparedStatement.execute();
                    MessageBox.showInfo(Alert.AlertType.INFORMATION,"Ajout avec succes!","Ajout");
                    this.listeSejour();
                    this.clear();
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @FXML
    void onBtnAnnuléClicked(ActionEvent event) {
        clear();
    }

    @FXML
    void onBtnModifierClicked(ActionEvent event) {
        connection = ConnexionDB.mydb();

        // recuperation les valeur dans la formulaire
        int idSejour = Integer.parseInt(id_sejour.getText());
        String numChambre = id_numeri_chambre.getValue();
        Date dateEntre = Date.valueOf(date_entre.getValue());
        int nombreJour = Integer.parseInt(id_nombre_jour.getText());
        String nomClient = id_nom_de_client.getText();
        String idTel = id_telephone.getText();

        // Verification si les champs ne sont pas vide
        if(idSejour < 0 || nomClient.isEmpty() || nombreJour < 0 || numChambre.isEmpty() || idTel.isEmpty()){
            MessageBox.showInfo(Alert.AlertType.ERROR,"Veillez verifier le champ vide","Nouveau Sejour");
        } else {
            String sql = "UPDATE sejourner SET numchambr = ?,dateentreesejour = ?, nbrjour = ?, nomclient = ?, telephone = ? WHERE idsejour=?;";

            try{
                preparedStatement =connection.prepareStatement(sql);
                preparedStatement.setString(1,numChambre);
                preparedStatement.setDate(2,dateEntre);
                preparedStatement.setInt(3,nombreJour);
                preparedStatement.setString(4,nomClient);
                preparedStatement.setString(5,idTel);
                preparedStatement.setInt(6,idSejour);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if(alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.execute();
                    MessageBox.showInfo(Alert.AlertType.INFORMATION,"Modification Succes","Modification Sejour");
                    this.listeSejour();
                    this.clear();
                    this.toggleButtonsModifierSupprimer(false, "NOUVEAU SEJOUR");
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Erreur lors de l'execution de la requete : "+e.getMessage());
            }
        }
    }

    @FXML
    void onBtnSupprimerClicked(ActionEvent event) {
        this.toggleButtonsModifierSupprimer(false, "NOUVEAU SEJOUR");
        Sejour sejour = table_sejour.getSelectionModel().getSelectedItem();
        String sql = "DELETE FROM sejourner WHERE idsejour = ?;";

        if(sejour == null){
            MessageBox.showInfo(Alert.AlertType.ERROR,"Veuillez selectionner une ligne!","SUPPRESSION");
        } else {
            connection = ConnexionDB.mydb();

            try{
                assert connection != null;
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,sejour.getId_sejour());

                Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Confirmation");

                if (alert.showAndWait().get() == ButtonType.OK){
                    preparedStatement.executeUpdate();
                    this.listeSejour();
                    this.clear();
                    alert.setContentText("Supprimer avec succes");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onSearchText(KeyEvent event) {
        String rechercher = recherche.getText();
        if(rechercher.isEmpty()) this.listeSejour();
        else{
            data.clear();
            connection = ConnexionDB.mydb();
            String sql = "SELECT * FROM sejourner;";

            try {
                assert connection != null;
                preparedStatement = connection.prepareStatement(sql);
                resultSet =preparedStatement.executeQuery();

                List<Sejour>sejours =new ArrayList<>();
                while(resultSet.next()){
                    sejours.add(new Sejour(resultSet.getInt("idsejour"),resultSet.getString("nomclient"),
                            resultSet.getDate("dateentreesejour"),resultSet.getInt("nbrjour"),resultSet.getString("numchambr"),
                            resultSet.getString("telephone")));
                }

                for (Sejour sejour : sejours){
                    if (sejour != null){
                        if (
                                sejour.getId_numeri_chambre() != null &&sejour.getId_numeri_chambre().toLowerCase().contains(rechercher.toLowerCase())||
                                        sejour.getId_nom_de_client() != null &&sejour.getId_nom_de_client().toLowerCase().contains(rechercher.toLowerCase())||
                                        sejour.getId_telephone() != null &&sejour.getId_telephone().toLowerCase().contains(rechercher.toLowerCase())
                        ){
                            data.add(sejour);
                        }
                    }
                }

                table_sejour.setItems(data);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void t(RotateEvent event) {

    }

    public void listeSejour(){
        this.genererID();
        data.clear();

        connection = ConnexionDB.mydb();
        String sql = "SELECT * FROM sejourner;";

        try {
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            //Parcourir la table
            while (resultSet.next()){
                int idSejour = resultSet.getInt("idsejour");
                String numChambre = resultSet.getString("numchambr");
                Date dateEntre = resultSet.getDate("dateentreesejour");
                int nbrJour = resultSet.getInt("nbrjour");
                String nomClient = resultSet.getString("nomclient");
                String tel = resultSet.getString("telephone");

                data.add(new Sejour(idSejour,nomClient,dateEntre,nbrJour,numChambre,tel));

                //inserer une val dans la colonne
                col_id_sejour.setCellValueFactory(new PropertyValueFactory<Sejour, Integer>("id_sejour"));
                col_numero_chambre.setCellValueFactory(new PropertyValueFactory<Sejour, String >("id_numeri_chambre"));
                col_date_entrer.setCellValueFactory(new PropertyValueFactory<Sejour, Date>("date_entre"));
                col_nombre_de_jour.setCellValueFactory(new PropertyValueFactory<Sejour, Integer>("id_nombre_jour"));
                col_nom.setCellValueFactory(new PropertyValueFactory<Sejour, String>("id_nom_de_client"));
                col_telephone.setCellValueFactory((new PropertyValueFactory<Sejour, String>("id_telephone")));
            }

            //inserer dans la table de formulaire
            table_sejour.setItems(data);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Clear data
    public void clear(){
        id_nom_de_client.setText("");
        date_entre.setValue(null);// Effacer la valeur du champ de type date
        id_nombre_jour.setText("");
        id_nom_de_client.setText("");
        id_telephone.setText("");
    }

    //recuperer ladernier IDde la BDD
    public void genererID(){
        Integer nextID = this.getLastID() + 1;
        id_sejour.setText(String.valueOf(nextID));
        id_numeri_chambre.getItems().clear();

        connection = ConnexionDB.mydb();
        String sql = "SELECT numchambr FROM chambre";
        List<String> list =new ArrayList<>();

        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String numChambre = resultSet.getString("numchambr");
                list.add(numChambre);
            }
            String[] array = list.toArray(new String[0]);
            for (String item : array) {
                id_numeri_chambre.getItems().add(item);
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Integer getLastID(){
        String sql = "SELECT idsejour FROM sejourner ORDER BY idsejour DESC LIMIT 1;";
        connection = ConnexionDB.mydb();

        try{
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            Integer lastID = 0;

            while (resultSet.next()){
                lastID =resultSet.getInt("idsejour");
            }

            return lastID;
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void visibleExporterPdf(boolean arg){
        export_pdf.setVisible(arg);
    }

    public void genererPdf(){
        int idSejour = Integer.parseInt(id_sejour.getText());
        String numChambre = id_numeri_chambre.getValue();
        Date dateEntre = Date.valueOf(date_entre.getValue());
        int nbrjour = Integer.parseInt(id_nombre_jour.getText());
        String nomClient = id_nom_de_client.getText();
        String telephone = id_telephone.getText();

        String chemainPdf = "D:\\"+nomClient+"_fiche.pdf";

        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document,page);
            contentStream.setFont(PDType1Font.HELVETICA,12);

            //Titre
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD,14);
            contentStream.newLineAtOffset(50,page.getMediaBox().getHeight() - 50);
            contentStream.showText("Reçu de sejour");
            contentStream.endText();

            //information
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA,12);
            contentStream.setLeading(14);
            contentStream.newLineAtOffset(50,page.getMediaBox().getHeight() - 80);
            contentStream.showText("Numero Sejour: "+idSejour);
            contentStream.newLine();
            contentStream.showText("Nom client: "+nomClient);
            contentStream.newLine();
            contentStream.showText("Designation de chambre: "+numChambre);
            contentStream.newLine();
            contentStream.showText("Nombre de jours"+nbrjour);
            contentStream.newLine();
            contentStream.showText("Date d'entrée : "+new SimpleDateFormat("dd/MM/yyyy").format(dateEntre));
            contentStream.endText();

            contentStream.close();


            File file = new File("D:\\pdf\\"+nomClient+"_fiche.pdf");
            document.save(file);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generation PDF");
            alert.setHeaderText(null);
            alert.setContentText("Le fichier pdf a été généré avec succes.");
            alert.showAndWait();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void onExportPDF(ActionEvent event) {
        this.genererPdf();
    }
}
