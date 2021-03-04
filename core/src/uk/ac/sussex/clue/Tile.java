package uk.ac.sussex.clue;

public class Tile {
    // The player currently occupying this tile, if any
    private Player player;
    // Our ClueGame screen
    private ClueGame screen;

    public Tile(ClueGame screen) {
        this.screen = screen;
    }
    public void onEnter(Player player) {
        this.player = player;
        return;
    }

    public boolean canEnter(Player player) {
        return player == null;
    }

    public void onLeave() {
        setPlayer(null);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
