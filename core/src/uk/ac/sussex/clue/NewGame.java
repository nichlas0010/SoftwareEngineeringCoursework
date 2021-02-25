package uk.ac.sussex.clue;

public class NewGame extends Window {
    public NewGame(MainController mc) {
        super(mc);
        // TODO: New game menu
        mc.setScreen(new ClueGame(mc));
    }
}
