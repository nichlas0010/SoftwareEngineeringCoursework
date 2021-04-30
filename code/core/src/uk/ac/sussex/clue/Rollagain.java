package uk.ac.sussex.clue;

/**
 * An extension of the {@link Tile}, except when you enter it you get to roll again
 */
public class Rollagain extends Tile {
    // Whether or not we've been used this turn
    private boolean hasBeenUsed = false;

    public Rollagain(ClueGame screen) {
        super(screen);
    }

    /**
     * Called when you enter the tile
     *
     * If this tile hasn't been entered this turn, it gives you another turn.
     *
     * @see {@link GameState#reRoll()}
     * @param p The player who entered the tile
     * @param isPulled Whether or not they were pulled in here
     */
    @Override
    public void onEnter(Player p, boolean isPulled) {
        super.onEnter(p, isPulled);

        // If we haven't been entered this turn
        if(!hasBeenUsed) {
            // Make sure we can't be entered again
            hasBeenUsed = true;
            // Then re-roll
            screen.getGameState().reRoll();
        }
    }

    /**
     * Resets our tile so we can be used again
     */
    @Override
    public void reset() {
        hasBeenUsed = false;
    }
}
