package rd.project.api;

public enum Genres {
    Action(28, "Action"),
    Adventure(12, "Adventure"),
    Animation(15, "Animation"),
    Comedy(35,"Comedy"),
    Crime(80, "Crime"),
    Documentary(99, "Documentary"),
    Family(10751, "Family"),
    Drama(18, "Drama"),
    Fantasy(14, "Fantasy"),
    History(36,"History"),
    Horror(27,"Horror"),
    Music(10402,"Music"),
    Mystery(9648,"Mystery"),
    Romance(10749,"Romance"),
    ScienceFiction(878,"Science-Fiction"),
    TV_Movie(10770, "TV Movie"),
    Thriller(53, "Thriller"),
    War(10752, "War"),
    Western(37, "Western");


    private int id;
    private String name;

    Genres(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return Integer.toString(id);
    }

    public String getName() {
        return name;
    }
}
