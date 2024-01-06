import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class WelcomeSceneController {

    @FXML
    BorderPane screen;

    public void switchToInputScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InputScene.fxml"));
            Parent root = loader.load();
            Scene scene = ((Node)event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
