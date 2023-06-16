package kaznarah.reservation_chambre_hotel.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnexionDB {
    private static Connection connection = null;
    private static String driver = "org.postgresql.Driver";

    public static Connection mydb(){
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test1","postgres","kazz");
            System.out.println("Connected succeffuly");
            return connection;


        } catch (Exception e) {
           e.printStackTrace();
           return null;
        }
    }
}
