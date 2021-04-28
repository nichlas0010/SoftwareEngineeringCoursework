package uk.ac.sussex.clue;

public class Rollagain extends Tile {
    public Rollagain(ClueGame screen) {
        super(screen);
    }

    @Override
    public void onEnter(Player p, boolean isPulled) {
        super.onEnter(p, isPulled);

        screen.getGameState().reRoll();
    }
}
