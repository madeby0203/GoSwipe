package rd.project.api;

/**
 * Providers and their corresponding IDs for the API.
 * In this case only some popular international and Dutch providers are implemented
 */

public enum Providers {
    Netflix(8, "Netflix"),
    Amazon(9, "Amazon"),
    DisneyPlus(337, "Disney+"),
    Videoland(72, "Videoland"),
    AppleTVPlus(350, "Apple TV+"),
    NPOstart(360, "NPO start");

    private int id;
    private String name;

    Providers(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return Integer.toString(id);
    }

    public String getName() {
        return name;
    }
}
