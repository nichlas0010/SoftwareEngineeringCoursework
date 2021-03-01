package uk.ac.sussex.clue;

public class NewGame extends Window {
    public NewGame() {
        super();
        // TODO: New game menu
        MainController.instance.setScreen(new ClueGame("sp;bc;")); // "Scarlett Player, Blum Computer". Config for the game setup.
    }
}
