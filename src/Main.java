import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("WelcomeScene.fxml")));
        Scene welcomeScene = new Scene(root);
        stage.setScene(welcomeScene);
        stage.widthProperty().addListener((observable) -> {
            if (stage.getWidth() < 600) {
                stage.setWidth(600);
            }
        });
        stage.heightProperty().addListener((observable) -> {
            if (stage.getHeight() < 400) {
                stage.setHeight(400);
            }
        });
        stage.show();
    }
}
