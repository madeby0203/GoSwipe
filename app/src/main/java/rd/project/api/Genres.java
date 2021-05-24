package rd.project.api;

public enum Genres {
    Action(28),
    Adventure(12),
    Animation(15),
    Comedy(35),
    Crime(80),
    Documentary(99),
    Family(10751),
    Drama(18),
    Fantasy(14),
    History(36),
    Horror(27),
    Music(10402),
    Mystery(9648),
    Romance(10749),
    ScienceFiction(878),
    TV_Movie(10770),
    Thriller(53),
    War(10752),
    Western(37);


    private int id;

    Genres(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
