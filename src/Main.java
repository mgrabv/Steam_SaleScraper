import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeScene.fxml"));
        Parent root = loader.load();
        ((MainController)loader.getController()).setMinWindowSize(stage);
        Scene welcomeScene = new Scene(root);
        stage.setScene(welcomeScene);
        stage.show();
    }
}
