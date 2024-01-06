import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ScraperUtils {
    private static String genre;
    private static int scrollStart = 0;
    private static boolean scrapingMore = false;
    public static Task<Void> scrapingTask;


    public static List<Game> scrape() {

        List<Game> scrapedGames = new ArrayList<>();
        String genreTag = getGenreTag(genre);

        try {
            // Points to infinite scrolling list of Steam Global Top Sellers in JSON format
            URL steamTopSellers = new URI("https://store.steampowered.com/search/results/?query&start=" + scrollStart + "&count=50&dynamic_data=&sort_by=_ASC&supportedlang=english&specials=1&snr=1_7_7_globaltopsellers_7&filter=globaltopsellers&infinite=1" + genreTag).toURL();
            String HTMLContent = getPageHTMLContent(steamTopSellers);
            String HTMLGameListings = getListingsHTML(HTMLContent);
            scrapedGames = extractAndProcessSales(HTMLGameListings);
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }


        return scrapedGames;
    }

    public static void setGenre(String genreName) {
        genre = genreName;
    }

    public static void clearScraper() {
        scrollStart = 0;
    }

    public static List<Game> scrapeMore() {
        scrollStart += 50;
        scrapingMore = true;
        return scrape();
    }

    private static String getPageHTMLContent(URL page) {
        try {
            // Saves page content in String form
            StringBuilder HTMLContent = new StringBuilder();
            Scanner sc = new Scanner(page.openStream());
            while (sc.hasNextLine()) {
                HTMLContent.append(sc.nextLine());
            }
            sc.close();
            return HTMLContent.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getListingsHTML(String HTMLContent){
        //Parses HTML content to JSON format to extract game listings in HTML string
        JsonObject JSONContent = (JsonObject) JsonParser.parseString(HTMLContent);
        return JSONContent.get("results_html").getAsString();
    }

    //This whole method is absolutely disgusting and will be optimised when I will find some time and motivation
    //But it works :)
    // --TODO: Re-structure so that it gets all info from listing snippet - if not only then proceed to game's page and get all info from there
    //(now it's a total mix of things)
    private static List<Game> extractAndProcessSales(String HTMLGameListings) {
        List<Game> scrapedGames = new ArrayList<>();

        scrapingTask = new Task<>() {
            @Override
            protected Void call() {

                Document doc = Jsoup.parse(HTMLGameListings);
                Elements games = doc.select("a");

                for (Element game : games) {
                    String title = game.children().select("[class=title]").text();
                    String price;
                    String ogPrice;
                    String discountPercent;
                    String gamePage;
                    String cover;
                    String description = "No description";
                    String releaseDate;
                    String reviews = "No user reviews";

                    gamePage = game.attr("abs:href");
                    cover = game.children().select("img").attr("abs:src");

                    if (game.stream().anyMatch(x -> x.hasClass("discount_final_price"))) {
                        if (game.stream().anyMatch(x -> x.hasClass("discount_final_price free"))) {
                            price = "Free";
                        } else {
                            price = game.children().select("[class=discount_final_price]").text().replaceAll("\\s", "");
                        }
                    } else {

                        try {
                            price = getDiscountPrice(new URI(gamePage).toURL());
                        } catch (URISyntaxException | MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (game.stream().anyMatch(x -> x.hasClass("discount_original_price"))) {
                        ogPrice = game.children().select("[class=discount_original_price]").text().replaceAll("\\s", "");
                    } else {

                        try {
                            ogPrice = getOriginalPrice(new URI(gamePage).toURL());
                        } catch (MalformedURLException | URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    //Additional check & getDiscountPrice repeat because my beloved Steam FrontEnd devs often mess up the listings,
                    //and the discount ends up existing only inside the exclusive game page
                    if (price.equals(ogPrice) && !price.equalsIgnoreCase("Free")) {
                        try {
                            price = getDiscountPrice(new URI(gamePage).toURL());
                        } catch (URISyntaxException | MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (game.stream().anyMatch(x -> x.hasClass("discount_pct"))) {
                        discountPercent = game.children().select("[class=discount_pct]").text();
                    } else {
                        if (price.equalsIgnoreCase("free") && ogPrice.equalsIgnoreCase("free")) {
                            discountPercent = "-0%";
                        } else if (price.equalsIgnoreCase("free") && !ogPrice.equalsIgnoreCase("free")) {
                            discountPercent = "-100%";
                        }
                        else {
                            discountPercent = "-" + (int) Math.round((Double.parseDouble(ogPrice.substring(0, ogPrice.length() - 2).replaceAll(",", ".")) - Double.parseDouble(price.substring(0, price.length() - 2).replaceAll(",", "."))) / Double.parseDouble(ogPrice.substring(0, ogPrice.length() - 2).replaceAll(",", ".")) * 100) + "%";
                        }
                    }

                    try {
                        String HTMLContent = getPageHTMLContent(new URI(gamePage).toURL());
                        Document gameDoc = Jsoup.parse(HTMLContent);
                        Element gameDetails = gameDoc.select("[class=glance_ctn]").first();

                        if (!gameDetails.children().select("[class=game_description_snippet]").text().isEmpty()) {
                            description = gameDetails.children().select("[class=game_description_snippet]").text();
                        }

                        releaseDate = gameDetails.select("[class=date]").text();
                        int allReviewsIndex = 0;

                        if (gameDetails.stream().anyMatch(x -> x.text().equalsIgnoreCase("Recent Reviews:"))) {
                            allReviewsIndex = 1;
                        }

                        if (gameDetails.stream().anyMatch(x -> x.hasClass("game_review_summary not_enough_reviews"))) {
                            reviews = "No overall rating\n" + gameDetails.stream().filter(x -> x.hasClass("game_review_summary")).toList().get(allReviewsIndex).text();
                        } else if (gameDetails.stream().anyMatch(x -> x.hasClass("game_review_summary"))) {
                            reviews = "Overall rating:\n" + gameDetails.stream().filter(x -> x.hasClass("game_review_summary")).toList().get(allReviewsIndex).text() +
                                    "\nRated by: " + gameDetails.stream().filter(x -> x.hasClass("responsive_hidden")).toList().get(allReviewsIndex).text().
                                    replaceAll(Arrays.toString(new String[]{"(", ")"}), "") + " user(s)";
                        }

                    } catch (MalformedURLException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }

                    scrapedGames.add(new Game(title, ogPrice, price, discountPercent, gamePage, cover, description, releaseDate, reviews));
                    //System.out.println(title + "|" + ogPrice + "|" + price + "|" + discountPercent + "|" + gamePage + "|" + cover);
                    updateProgress(scrapedGames.size(), games.size());
                    updateMessage(scrapedGames.size() * 100 / games.size() + "%");
                }
                return null;
            }
        };

        if (scrapingMore) {
            ScrapedSceneController.controller.moreBar.progressProperty().bind(scrapingTask.progressProperty());
            ScrapedSceneController.controller.moreLabel.textProperty().bind(scrapingTask.messageProperty());
        } else {
            InputSceneController.controller.scrapeBar.progressProperty().bind(scrapingTask.progressProperty());
            InputSceneController.controller.barPercent.textProperty().bind(scrapingTask.messageProperty());
        }

        Thread thread = new Thread(scrapingTask);
        thread.start();
        scrapingMore = false;

        return scrapedGames;
    }

    public static boolean genreExists(String genreName) {
        Elements genreTags = getGenreTags();
        String genre = genreName.toUpperCase().trim();
        return (genreTags.stream().anyMatch(x -> x.text().toUpperCase().equals(genre))) || genreName.equals("ALL");
    }

    private static String getGenreTag(String genreName) {
        Elements genreTags = getGenreTags();
        String genre = genreName.toUpperCase().trim();

        if (genre.equals("ALL")) {
            return "";
        }
        else {
            return "&tags=" + genreTags.stream().filter(x -> x.text().toUpperCase().equals(genre)).findFirst().get().attr("data-tagid");
        }

    }

    private static String getDiscountPrice(URL gamePage) {
        String HTMLContent = getPageHTMLContent(gamePage);
        Document doc = Jsoup.parse(HTMLContent);

        if (doc.select("[class=game_purchase_price price]").text().equalsIgnoreCase("free to play")) {
            return "Free";
        }

        String result = doc.select("[class=discount_final_price]").first().text().replaceAll("\\s", "");

        if (result.isEmpty()) {
            return "No data";
        }

        return result;
    }

    private static String getOriginalPrice(URL gamePage) {
        String HTMLContent = getPageHTMLContent(gamePage);
        Document doc = Jsoup.parse(HTMLContent);

        if (doc.select("td").hasClass("normal_price")) {
            return doc.select("[class=normal_price]").first().text().replaceAll("\\s", "");
        }

        String result = doc.select("[class=discount_original_price]").first().text().replaceAll("\\s", "");

        if (result.isEmpty()) {
            return "No data";
        }

        return result;
    }

    private static Elements getGenreTags() {
        Elements genreTags;

        try {
            String genreTagsURL = "https://store.steampowered.com/tag/browse";
            Document doc = Jsoup.connect(genreTagsURL).get();
            genreTags = doc.select("[class=tag_browse_tag]");
        } catch (IOException e) {
            throw new RuntimeException();
        }

        if (genreTags == null || genreTags.isEmpty()) {
            throw new RuntimeException("Something went wrong with collecting available game genre tags from Steam");
        }

        return genreTags;
    }
}