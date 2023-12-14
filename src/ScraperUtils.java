import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private static int searchResults = 0;
    private static int resultCap = 100;

    public static List<String> scrape(String genreName, int resultCap) {
        String genre = genreName.toUpperCase().trim();

        if (!genreExists(genreName)) {
            return null;
        }

        List<String> scrapedGames = new ArrayList<>();
        String genreTag = getGenreTag(genre);
        ScraperUtils.resultCap = resultCap;
        int scrollStart = 0;

        //System.out.print("TITLE | ORIGINAL PRICE | DISCOUNTED PRICE | DISCOUNT PERCENT\n");

        while (searchResults < resultCap) {
            try {
                // Points to infinite scrolling list of Steam Global Top Sellers in JSON format
                URL steamTopSellers = new URI("https://store.steampowered.com/search/results/?query&start=" + scrollStart + "&count=50&dynamic_data=&sort_by=_ASC&supportedlang=english&snr=1_7_7_globaltopsellers_7&filter=globaltopsellers&infinite=1" + genreTag).toURL();
                String HTMLContent = getPageHTMLContent(steamTopSellers);
                String HTMLGameListings = getListingsHTML(HTMLContent);
                scrapedGames = extractAndProcessSales(HTMLGameListings);
                scrollStart += 50;
            } catch (URISyntaxException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return scrapedGames;
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
        Document doc = Jsoup.parse(HTMLGameListings);
        Elements games = doc.select("a");
        for (Element game : games) {
            if (searchResults >= resultCap) {
                break;
            }
            if (game.stream().anyMatch(x -> x.hasClass("discount_block search_discount_block"))) {
                String title = game.children().select("[class=title]").text();
                String price = game.children().select("[class=discount_final_price]").text().replaceAll("\\s", "");
                String ogPrice;

                if (game.stream().anyMatch(x -> x.hasClass("discount_original_price"))) {
                    ogPrice = game.children().select("[class=discount_original_price]").text().replaceAll("\\s", "");
                }
                else {
                    try {
                        ogPrice = getOriginalPrice(new URI(game.attr("abs:href")).toURL());
                    } catch (MalformedURLException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
                int discountPercent = (int)Math.round((Double.parseDouble(ogPrice.substring(0, ogPrice.length() - 2).replaceAll(",",".")) - Double.parseDouble(price.substring(0, price.length() - 2).replaceAll(",","."))) / Double.parseDouble(ogPrice.substring(0, ogPrice.length() - 2).replaceAll(",",".")) * 100);
                scrapedGames.add(title + " | " + ogPrice + " | " + price + " | " + "-" + discountPercent + "%");
                searchResults++;
            }
        }
        return scrapedGames;
    }

    private static boolean genreExists(String genreName) {
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
        return doc.select("[class=normal_price]").first().text().replaceAll("\\s", "");
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