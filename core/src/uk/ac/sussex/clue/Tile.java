package uk.ac.sussex.clue;

public class Tile {

    public void onEnter(Player player) {
        return;
    }

    public boolean canEnter(Player player) {
        ClueGame screen = (ClueGame) MainController.instance.getScreen();
        for(Player p : screen.getPlayers()) {
            return true;
        }
        return false;
    }
}
