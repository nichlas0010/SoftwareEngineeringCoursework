package uk.ac.sussex.clue;

public class Freesuggestion extends Tile {
    // Whether or not we've been used this turn
    private boolean hasBeenUsed = false;

    public Freesuggestion(ClueGame screen) {
        super(screen);
    }

    @Override
    public void onEnter(Player p, boolean isPulled) {
        super.onEnter(p, isPulled);
        if(!hasBeenUsed) {
            hasBeenUsed = true;
            screen.getGameState().reGuess();
        }
    }

    @Override
    public void reset() {
        hasBeenUsed = false;
    }
}
