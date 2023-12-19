import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;

public class MainController {
    private Scene scene;
    private Stage stage;
    private Parent root;

    @FXML
    Text genreError, capError;
    @FXML
    TextField genreField, capField;


    public void switchToInputScene(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("InputScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void scrape() {
        List<String> res = ScraperUtils.scrape(genreField.getText(), Integer.parseInt(capField.getText()));
        for (String game : res) {
            System.out.println(game);
        }
    }
}
