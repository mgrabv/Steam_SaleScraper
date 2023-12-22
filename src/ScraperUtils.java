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
import java.util.List;
import java.util.Scanner;

public class ScraperUtils {
    private static String genre;
    private static int scrollStart = 0;
    public static Task<Void> scrapingTask;


    public static List<String> scrape() {

        List<String> scrapedGames = new ArrayList<>();
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

    public static List<String> scrapeMore() {
        scrollStart += 50;
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

    private static List<String> extractAndProcessSales(String HTMLGameListings) {
        List<String> scrapedGames = new ArrayList<>();

        scrapingTask = new Task<>() {
            @Override
            protected Void call() {

                Document doc = Jsoup.parse(HTMLGameListings);
                Elements games = doc.select("a");

                for (
                        Element game : games) {
                    String title = game.children().select("[class=title]").text();
                    String price = game.children().select("[class=discount_final_price]").text().replaceAll("\\s", "");
                    String ogPrice;
                    String discountPercent;
                    String gamePage;
                    String cover;

                    gamePage = game.attr("abs:href");
                    cover = game.children().select("img").attr("abs:src");

                    if (game.stream().anyMatch(x -> x.hasClass("discount_original_price"))) {
                        ogPrice = game.children().select("[class=discount_original_price]").text().replaceAll("\\s", "");
                    } else {

                        try {
                            ogPrice = getOriginalPrice(new URI(gamePage).toURL());
                        } catch (MalformedURLException | URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (game.stream().anyMatch(x -> x.hasClass("discount_pct"))) {
                        discountPercent = game.children().select("[class=discount_pct]").text();
                    } else {
                        discountPercent = "-" + (int) Math.round((Double.parseDouble(ogPrice.substring(0, ogPrice.length() - 2).replaceAll(",", ".")) - Double.parseDouble(price.substring(0, price.length() - 2).replaceAll(",", "."))) / Double.parseDouble(ogPrice.substring(0, ogPrice.length() - 2).replaceAll(",", ".")) * 100) + "%";
                    }

                    scrapedGames.add(title + "|" + ogPrice + "|" + price + "|" + discountPercent + "|" + gamePage + "|" + cover);
                    //System.out.println(title + "|" + ogPrice + "|" + price + "|" + discountPercent + "|" + gamePage + "|" + cover);
                    updateProgress(scrapedGames.size(), 50);
                    updateMessage(scrapedGames.size() * 100 / 50 + "%");
                }
                return null;
            }
        };

        MainController.controller.scrapeBar.progressProperty().bind(scrapingTask.progressProperty());
        MainController.controller.barPercent.textProperty().bind(scrapingTask.messageProperty());
        Thread thread = new Thread(scrapingTask);
        thread.start();

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

    private static String getOriginalPrice(URL gamePage) {
        String HTMLContent = getPageHTMLContent(gamePage);
        Document doc = Jsoup.parse(HTMLContent);

        if (doc.select("td").hasClass("normal_price")) {
            return doc.select("[class=normal_price]").first().text().replaceAll("\\s", "");
        }

        return doc.select("[class=discount_original_price]").first().text().replaceAll("\\s", "");
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