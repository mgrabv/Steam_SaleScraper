import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeScene.fxml"));
        Parent root = loader.load();
        Scene welcomeScene = new Scene(root);
        stage.setScene(welcomeScene);
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.centerOnScreen();
        stage.setTitle("Steam Sale Scraper");
        stage.getIcons().add(new Image("GraphicalAssets/SteamScraperIcon.png"));
        stage.show();

        stage.setOnCloseRequest(windowEvent -> System.exit(1));
    }
}
