package uk.ac.sussex.clue;

public class NewGame extends Window {
    public NewGame() {
        // TODO: Actual logic

    }

    public void render(float delta) {
        MainController.instance.setScreen(new ClueGame("sp;wc")); // "Scarlett Player, white Computer". Config for the game setup.
    }
}
