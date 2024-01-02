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
    VBox loadingMorePane;
    @FXML
    ProgressBar moreBar;
    @FXML
    Label moreLabel;



    @FXML
    private void initialize() {
        controller = this;
        ogPriceColumn.setComparator((o1, o2) -> {
            double price1 = Double.parseDouble(o1.substring(0, o1.length() - 2).replaceAll(",", "."));
            double price2 = Double.parseDouble(o2.substring(0, o2.length() - 2).replaceAll(",", "."));
            return Double.compare(price1, price2);
        });
        priceColumn.setComparator((o1, o2) -> {
            double price1 = Double.parseDouble(o1.substring(0, o1.length() - 2).replaceAll(",", "."));
            double price2 = Double.parseDouble(o2.substring(0, o2.length() - 2).replaceAll(",", "."));
            return Double.compare(price1, price2);
        });
        discountColumn.setComparator((o1, o2) -> {
            int discount1 = Integer.parseInt(o1.substring(1, o1.length() - 1));
            int discount2 = Integer.parseInt(o2.substring(1, o2.length() - 1));
            return Double.compare(discount1, discount2);
        });

//        gameTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Game>() {
//            @Override
//            public void changed(ObservableValue<? extends Game> observableValue, Game game, Game t1) {
//                if (detailsPlaceholder.isVisible()) {
//                    detailsPlaceholder.setVisible(false);
//                    detailsPane.setVisible(true);
//                }
//                gameCover.setImage(game.getCover().getImage());
//                gameTitle.setText(game.getTitle());
//                gameDescription.setText(game.getDescription());
//                gameReleaseDate.setText(game.getReleaseDate());
//                gameReviews.setText(game.getReviews());
//            }
//        });

        loadResults();
    }

//    @FXML
//    private void handleRowSelect() {
//        Game selected = gameTable.getSelectionModel().getSelectedItem();
//
//        if (detailsPlaceholder.isVisible()) {
//            detailsPlaceholder.setVisible(false);
//            detailsPane.setVisible(true);
//        }
//
//        gameCover.setImage(selected.getCover().getImage());
//        gameTitle.setText(selected.getTitle());
//        gameDescription.setText(selected.getDescription());
//        gameReleaseDate.setText(selected.getReleaseDate());
//        gameReviews.setText(selected.getReviews());
//    }

    public void loadResults() {
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

        ObservableList<Game> games = FXCollections.observableArrayList();
        games.addAll(scrapedGames);
//        String[] g1 = "The Binding of Isaac: Afterbirth|39,99zł|23,99zł|-40%|https://store.steampowered.com/app/401920/The_Binding_of_Isaac_Afterbirth/?snr=1_7_7_2300_150_1|https://cdn.akamai.steamstatic.com/steam/apps/401920/capsule_sm_120.jpg?t=1573196132".split("\\|");
//        String[] g2 = "DEATH STRANDING DIRECTOR'S CUT|42,25zł|21,12zł|-50%|https://store.steampowered.com/app/1850570/DEATH_STRANDING_DIRECTORS_CUT/?snr=1_7_7_2300_150_1|https://cdn.akamai.steamstatic.com/steam/apps/1850570/capsule_sm_120.jpg?t=1700566729".split("\\|");
//        String[] g3 = "The Elder Scrolls V: Skyrim Anniversary Upgrade|79,00zł|39,50zł|-50%|https://store.steampowered.com/app/1746860/The_Elder_Scrolls_V_Skyrim_Anniversary_Upgrade/?snr=1_7_7_2300_150_1|https://cdn.akamai.steamstatic.com/steam/apps/1746860/capsule_sm_120.jpg?t=1701807368".split("\\|");
//        String[] g4 = "Batman™: Arkham Knight|71,99zł|14,39zł|-80%|https://store.steampowered.com/app/208650/Batman_Arkham_Knight/?snr=1_7_7_2300_150_1|https://cdn.akamai.steamstatic.com/steam/apps/208650/capsule_sm_120.jpg?t=1702934528".split("\\|");
//        String[] g5 = "Subnautica: Below Zero|107,99zł|53,99zł|-50%|https://store.steampowered.com/app/848450/Subnautica_Below_Zero/?snr=1_7_7_2300_150_1|https://cdn.akamai.steamstatic.com/steam/apps/848450/capsule_sm_120.jpg?t=1700522327".split("\\|");
//        games.add(new Game(g1[0], g1[1], g1[2], g1[3], g1[4], g1[5], g1[6], g1[7], g1[8]));
//        games.add(new Game(g2[0], g2[1], g2[2], g2[3], g2[4], g2[5], g2[6], g2[7], g2[8]));
//        games.add(new Game(g3[0], g3[1], g3[2], g3[3], g3[4], g3[5], g3[6], g3[7], g3[8]));
//        games.add(new Game(g4[0], g4[1], g4[2], g4[3], g4[4], g4[5], g4[6], g4[7], g4[8]));
//        games.add(new Game(g5[0], g5[1], g5[2], g5[3], g5[4], g5[5], g5[6], g5[7], g5[8]));
        gameTable.setItems(games);
    }

    public static void setGamesForTable(List<Game> games) {
//        if (scrapedGames == null) {
//            scrapedGames = games;
//        } else {
//            scrapedGames.addAll(games);
//        }
        scrapedGames.addAll(games);
    }

    public void visitStorePage() throws URISyntaxException, IOException {
        URI storePage = new URI(gameTable.getSelectionModel().getSelectedItem().getPage());
        Desktop.getDesktop().browse(storePage);
    }

    public void loadMore() {
        loadMore.setVisible(false);
        loadingMorePane.setVisible(true);

        List<Game> additionalGames = ScraperUtils.scrapeMore();
        Task<Void> scrapingTask = ScraperUtils.scrapingTask;
        scrapingTask.setOnSucceeded(workerStateEvent -> {
            setGamesForTable(additionalGames);
            loadResults();
            loadingMorePane.setVisible(false);
            loadMore.setVisible(true);
        });
    }

    public void switchToInputScene(ActionEvent event) {
        try {
            clearEnvironment();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InputScene.fxml"));
            Parent root = loader.load();
            Scene scene = ((Node)event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void clearEnvironment() {
        ScraperUtils.clearScraper();
        scrapedGames.clear();
    }
}
