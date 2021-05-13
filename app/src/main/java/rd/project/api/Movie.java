package rd.project.api;

public class Movie {

    private String overview;
    private String title;
    private String poster;
    private Number vote;
    private int id;

    /**
     * Movie and its corresponding information and features
     * @param overview
     * @param title
     * @param poster
     * @param vote
     * @param id
     */

    public Movie (String overview, String title, String poster, Number vote, int id) {
        this.overview = overview;
        this.title = title;
        this.poster = "https://image.tmdb.org/t/p/original" + poster;
        this.vote = vote;
        this.id = id;
    }

    public String toString() {
        return "Title: " + title + "\nOverview: " + overview + "\nVote: " + vote + "\nPoster: " + poster + "\nID: " + id;
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
}
