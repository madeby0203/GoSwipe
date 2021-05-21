package rd.project.api;

public class Movie {

    private final String overview;
    private final String title;
    private final String poster;
    private final Number vote;
    private final int id;
    private final String year;

    private final String genre;
    private final String platform;

    /**
     * Movie and its corresponding information and features
     * @param overview
     * @param title
     * @param poster
     * @param vote
     * @param id
     */

    public Movie (String overview, String title, String poster, Number vote, int id, String year, String genre, String platform) {
        this.overview = overview;
        this.title = title;
        this.poster = "https://image.tmdb.org/t/p/original" + poster;
        this.vote = vote;
        this.id = id;
        this.year = year;
        this.genre = genre;
        this.platform = platform;
    }

    public String toString() {
        return "Title: " + title + "\nOverview: " + overview + "\nVote: " + vote + "\nPoster: " + poster + "\nID: " + id +"\nYear: " + year + "\nGenre: " + genre + "\nPlatform: " + platform;
    }

    public Number getVote() {
        return vote;
    }

    public int getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlatform() {
        return platform;
    }

}
