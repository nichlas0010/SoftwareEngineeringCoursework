package uk.ac.sussex.clue;

public class Freesuggestion extends Tile {
    public Freesuggestion(ClueGame screen) {
        super(screen);
    }

    @Override
    public void onEnter(Player p, boolean isPulled) {
        super.onEnter(p, isPulled);
        screen.getGameState().reGuess();
    }
}
