package rd.project;

public class Movies {

    int image;
    String genre;
    int year;
    int score;
    String platform;

    public Movies(int image, String genre, int year, int score, String platform){
        this.image = image;
        this.genre = genre;
        this.year = year;
        this.score = score;
        this.platform = platform;
    }

    public int getImage() {
        return image;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public int getScore() {
        return score;
    }

    public String getPlatform() {
        return platform;
    }

}
