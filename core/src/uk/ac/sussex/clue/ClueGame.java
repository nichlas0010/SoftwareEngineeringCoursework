package uk.ac.sussex.clue;

import java.util.ArrayList;

public class ClueGame extends Window {
    ArrayList<Player> players;
    public ClueGame(String config) {

        players = new ArrayList<Player>();

        // TODO: config handling.

        players.add(new Player());

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
