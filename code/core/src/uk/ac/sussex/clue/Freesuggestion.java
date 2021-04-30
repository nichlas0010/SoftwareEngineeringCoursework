package uk.ac.sussex.clue;

/**
 * A normal tile, except you get to make a suggestion when you step on it
 */
public class Freesuggestion extends Tile {
    // Whether or not we've been used this turn
    private boolean hasBeenUsed = false;

    /**
     * Creates our tile
     * @see {@link Tile#Tile(ClueGame)}
     * @param screen our screen
     */
    public Freesuggestion(ClueGame screen) {
        super(screen);
    }

    /**
     * Calls {@link GameState#reGuess()} when we stop on it
     * @param p the player who entered
     * @param isPulled Whether or not they were pulled in here
     */
    @Override
    public void onEnter(Player p, boolean isPulled) {
        super.onEnter(p, isPulled);
        // If we haven't already been used this turn, make a suggestion
        if(!hasBeenUsed) {
            hasBeenUsed = true;
            screen.getGameState().reGuess();
        }
    }

    /**
     * Resets our used status. Should be called when a new turn starts
     */
    @Override
    public void reset() {
        hasBeenUsed = false;
    }
}
