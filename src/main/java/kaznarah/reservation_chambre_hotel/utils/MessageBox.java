package kaznarah.reservation_chambre_hotel.utils;

import javafx.scene.control.Alert;

public class MessageBox {
    public static void showInfo(Alert.AlertType alertType, String content, String title){
        Alert alert = new Alert(alertType);
        //alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.show();
    }
}
