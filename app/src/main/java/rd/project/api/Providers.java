package rd.project.api;

/**
 * Providers and their corresponding IDs for the API.
 * In this case only some popular international and Dutch providers are implemented
 */

public enum Providers {
    Netflix(8),
    Amazon(9),
    DisneyPlus(337),
    AppleTVPlus(350),
    Videoland(72),
    NPOstart(360);

    private int id;

    Providers(int id) {
        this.id = id;
    }

    public String getId() {
        return Integer.toString(id);
    }
}
