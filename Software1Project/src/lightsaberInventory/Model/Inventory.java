package lightsaberInventory.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Inventory {

    private static ObservableList<Client> allClients = FXCollections.observableArrayList();

    public static ObservableList<Client> getAllClients() {
        return allClients;
    }


    public static boolean isNumeric(String text) {
        try {
            // Attempt to parse as an integer
            Integer.parseInt(text);
            return true;  // If successful, it's numeric
        } catch (NumberFormatException eInt) {
            try {
                // Attempt to parse as a double
                Double.parseDouble(text);
                return true;  // If successful, it's numeric
            } catch (NumberFormatException eDouble) {
                return false;  // If both attempts fail, it's not numeric
            }
        }
    }

}
