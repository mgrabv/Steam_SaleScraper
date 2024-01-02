import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Game {

    private String title;
    private String ogPrice;
    private String price;
    private String discountPercent;
    private String page;
    private ImageView cover;
    private String description;
    private String releaseDate;
    private String reviews;



    public Game(String title, String ogPrice, String price, String discountPercent, String gamePage, String cover, String description, String releaseDate, String reviews) {
        this.title = title;
        this.ogPrice = ogPrice;
        this.price = price;
        this.discountPercent = discountPercent;
        this.page = gamePage;

        this.cover = new ImageView();
        this.cover.setImage(new Image(cover));
        this.cover.setPreserveRatio(true);
        this.cover.setFitWidth(200);

        this.description = description;
        this.releaseDate = releaseDate;
        this.reviews = reviews;
    }

    public String getTitle() {
        return title;
    }

    public String getOgPrice() {
        return ogPrice;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public String getPage() {
        return page;
    }

    public ImageView getCover() {
        return cover;
    }

    public String getDescription() {
        return description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getReviews() {
        return reviews;
    }
}
