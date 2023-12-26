public class Game {

    private String title;
    private String ogPrice;
    private String price;
    private String discountPercent;
    private String gamePage;
    private String cover;


    public Game(String title, String ogPrice, String price, String discountPercent, String gamePage, String cover) {
        this.title = title;
        this.ogPrice = ogPrice;
        this.price = price;
        this.discountPercent = discountPercent;
        this.gamePage = gamePage;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOgPrice() {
        return ogPrice;
    }

    public void setOgPrice(String ogPrice) {
        this.ogPrice = ogPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getGamePage() {
        return gamePage;
    }

    public void setGamePage(String gamePage) {
        this.gamePage = gamePage;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
