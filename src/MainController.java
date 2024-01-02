import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private Scene scene;
    private Parent root;
    public static MainController controller;
    private List<Game> scrapedGames = new ArrayList<>();

    @FXML
    Text genreError;
    @FXML
    TextField genreField;
    @FXML
    VBox inputPane, progressPane;
    @FXML
    ProgressBar scrapeBar;
    @FXML
    Label barPercent;


    public void setMinWindowSize(Stage stage) {
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
    }

    public void switchToInputScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InputScene.fxml"));
            root = loader.load();
            scene = ((Node)event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void switchToScrapedScene(ActionEvent event) {
        try {
            ScrapedSceneController.setGamesForTable(scrapedGames);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ScrapedScene.fxml"));
            root = loader.load();
            scene = ((Node)event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void scrape(ActionEvent event) {
        String genre = genreField.getText().trim().toUpperCase();
        if (!ScraperUtils.genreExists(genre)) {
            genreError.setVisible(true);
        } else {
            controller = this;
            inputPane.setVisible(false);
            progressPane.setVisible(true);

            ScraperUtils.setGenre(genre);
            scrapedGames = ScraperUtils.scrape();
            Task<Void> scrapingTask = ScraperUtils.scrapingTask;
            scrapingTask.setOnSucceeded(workerStateEvent -> switchToScrapedScene(event));
        }
    }
}
