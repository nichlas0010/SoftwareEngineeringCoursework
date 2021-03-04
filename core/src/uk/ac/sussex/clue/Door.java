package uk.ac.sussex.clue;

public class Door extends Tile {
    // the direction our room is in
    enum directions {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private directions dir;

    public Door(ClueGame screen, directions dir) {
        super(screen);
        this.dir = dir;
    }

    public void onEnter(Player player) {

    }
}
