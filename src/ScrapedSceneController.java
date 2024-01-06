import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ScrapedSceneController {

    private static List<Game> scrapedGames = new ArrayList<>();
    public static  ScrapedSceneController controller;

    @FXML
    TableView<Game> gameTable;
    @FXML
    TableColumn<Game, ImageView> coverColumn;
    @FXML
    TableColumn<Game, String> titleColumn;
    @FXML
    TableColumn<Game, String> ogPriceColumn;
    @FXML
    TableColumn<Game, String> priceColumn;
    @FXML
    TableColumn<Game, String> discountColumn;

    @FXML
    VBox detailsPane;
    @FXML
    VBox detailsPlaceholder;
    @FXML
    ImageView gameCover;
    @FXML
    Label gameTitle;
    @FXML
    Label gameDescription;
    @FXML
    Label gameReleaseDate;
    @FXML
    Label gameReviews;

    @FXML
    Button loadMore;
    @FXML
    Button changeGenre;
    @FXML
    VBox loadingMorePane;
    @FXML
    ProgressBar moreBar;
    @FXML
    Label moreLabel;
    @FXML
    Label noGames;
    @FXML
    Label noMoreGames;



    @FXML
    private void initialize() {
        controller = this;

        coverColumn.setCellValueFactory(new PropertyValueFactory<>("cover"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        ogPriceColumn.setCellValueFactory(new PropertyValueFactory<>("ogPrice"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercent"));
        gameTable.setRowFactory(gameTableView -> {
            TableRow<Game> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                Game selected = row.getItem();

                if (detailsPlaceholder.isVisible()) {
                    detailsPlaceholder.setVisible(false);
                    detailsPane.setVisible(true);
                }

                gameCover.setImage(selected.getCover().getImage());
                gameTitle.setText(selected.getTitle());
                gameDescription.setText(selected.getDescription());
                gameReleaseDate.setText(selected.getReleaseDate());
                gameReviews.setText(selected.getReviews());
            });
            return row;
        });

        ogPriceColumn.setComparator((o1, o2) -> {
            double price1;
            double price2;

            if (o1.equalsIgnoreCase("Free")) {
                price1 = 0.00;
            } else {
                price1 = Double.parseDouble(o1.substring(0, o1.length() - 2).replaceAll(",", "."));
            }
            if (o2.equalsIgnoreCase("Free")) {
                price2 = 0.00;
            } else {
                price2 = Double.parseDouble(o2.substring(0, o2.length() - 2).replaceAll(",", "."));
            }

            return Double.compare(price1, price2);
        });
        priceColumn.setComparator((o1, o2) -> {
            double price1;
            double price2;

            if (o1.equalsIgnoreCase("Free")) {
                price1 = 0.00;
            } else {
                price1 = Double.parseDouble(o1.substring(0, o1.length() - 2).replaceAll(",", "."));
            }
            if (o2.equalsIgnoreCase("Free")) {
                price2 = 0.00;
            } else {
                price2 = Double.parseDouble(o2.substring(0, o2.length() - 2).replaceAll(",", "."));
            }

            return Double.compare(price1, price2);
        });
        discountColumn.setComparator((o1, o2) -> {
            int discount1 = Integer.parseInt(o1.substring(1, o1.length() - 1));
            int discount2 = Integer.parseInt(o2.substring(1, o2.length() - 1));
            return Double.compare(discount1, discount2);
        });

        Main.stage.setMinWidth(1400);
        Main.stage.setMinHeight(900);
        Main.stage.centerOnScreen();

        loadResults();
    }

    public void loadResults() {
        if (scrapedGames.isEmpty()) {
            gameTable.setVisible(false);
            detailsPlaceholder.setVisible(false);
            noGames.setVisible(true);
        } else {
            ObservableList<Game> games = FXCollections.observableArrayList();
            games.addAll(scrapedGames);
            gameTable.setItems(games);
        }
    }

    public static void setGamesForTable(List<Game> games) {
        scrapedGames.addAll(games);
    }

    public void visitStorePage() throws URISyntaxException, IOException {
        URI storePage = new URI(gameTable.getSelectionModel().getSelectedItem().getPage());
        Desktop.getDesktop().browse(storePage);
    }

    public void loadMore() {
        loadMore.setVisible(false);
        changeGenre.setVisible(false);
        loadingMorePane.setVisible(true);

        List<Game> additionalGames = ScraperUtils.scrapeMore();
        Task<Void> scrapingTask = ScraperUtils.scrapingTask;
        scrapingTask.setOnSucceeded(workerStateEvent -> {
            if (additionalGames.isEmpty()) {
                loadingMorePane.setVisible(false);
                noMoreGames.setVisible(true);
                changeGenre.setVisible(true);
            } else {
                setGamesForTable(additionalGames);
                loadResults();
                loadingMorePane.setVisible(false);
                loadMore.setVisible(true);
                changeGenre.setVisible(true);
            }
        });
    }

    public void switchToInputScene(ActionEvent event) {
        try {
            clearEnvironment();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InputScene.fxml"));
            Parent root = loader.load();
            Scene scene = ((Node)event.getSource()).getScene();
            scene.setRoot(root);

            Main.stage.setMinWidth(600);
            Main.stage.setMinHeight(400);

            if (!Main.stage.isMaximized()) {
                Main.stage.setWidth(600);
                Main.stage.setHeight(400);
            }

            Main.stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void clearEnvironment() {
        ScraperUtils.clearScraper();
        scrapedGames.clear();
    }
}
