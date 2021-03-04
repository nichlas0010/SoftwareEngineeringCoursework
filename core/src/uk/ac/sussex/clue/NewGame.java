package uk.ac.sussex.clue;

public class NewGame extends Window {
    public NewGame() {
        super();
        // TODO: New game menu
        MainController.instance.setScreen(new ClueGame("sp;wc")); // "Scarlett Player, white Computer". Config for the game setup.
    }
}
