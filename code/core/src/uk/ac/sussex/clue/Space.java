package uk.ac.sussex.clue;

/**
 * Literally just an empty space
 *
 * Exists to make handling of tiles slightly easier, at the cost of like 10 MB of memory
 */
public class Space extends Tile {

    public Space(ClueGame screen) {
        super(screen);
    }

    /**
     * Overrides the lower class so we can't be entered
     * @return false
     */
    @Override
    public boolean canEnter() {
        return false;
    }

    /**
     * Overrides the lower class so we can't be clicked
     * @param p the Player that clicked on our tile. Will usually be the current player
     */
    @Override
    public void clicked(Player p) {
        return;
    }
}
