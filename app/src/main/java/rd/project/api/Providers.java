package rd.project.api;

public enum Providers {
    Netflix(8),
    Amazon(9),
    DisneyPlus(337),
    AppleTVPlus(350),
    Videoland(72),
    NPOstart(360);

    public int id;

    Providers(int id) {
        this.id = id;
    }

    public String getId() {
        return Integer.toString(id);
    }
}
