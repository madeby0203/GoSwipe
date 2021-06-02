package rd.project;

public class Settings {

    private String genre;
    private String rating;
    private String year;
    private String provider;

    public Settings(String genre, String rating, String year, String provider) {
        this.genre = genre;
        this.provider = provider;
        this.rating = rating;
        this.year = year;
    }

    public String getYear() { return year; }

    public String getRating() { return rating; }

    public String getGenre() { return genre; }

    public String getProvider() { return provider; }
}
