package uk.ac.sussex.clue;

public class Rollagain extends Tile {
    // Whether or not we've been used this turn
    private boolean hasBeenUsed = false;

    public Rollagain(ClueGame screen) {
        super(screen);
    }

    @Override
    public void onEnter(Player p, boolean isPulled) {
        super.onEnter(p, isPulled);

        if(!hasBeenUsed) {
            hasBeenUsed = true;
            screen.getGameState().reRoll();
        }
    }

    @Override
    public void reset() {
        hasBeenUsed = false;
    }
}
