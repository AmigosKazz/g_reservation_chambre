module kaznarah.reservation_chambre_hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires poi;
    requires poi.ooxml;
    requires org.apache.pdfbox;
    requires java.mail;
    requires activation;

    opens kaznarah.reservation_chambre_hotel to javafx.fxml, javafx.graphics;
    opens kaznarah.reservation_chambre_hotel.controllers to javafx.fxml;
    opens kaznarah.reservation_chambre_hotel.models to javafx.base;
    exports kaznarah.reservation_chambre_hotel;
}