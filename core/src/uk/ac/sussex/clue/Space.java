package uk.ac.sussex.clue;

public class Space extends Tile {

    public Space(ClueGame screen) {
        super(screen);
    }

    @Override
    public boolean canEnter() {
        return false;
    }

    @Override
    public void clicked(Player p) {
        return;
    }
}
