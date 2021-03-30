package uk.ac.sussex.clue;

import java.util.ArrayList;

public class Tile {
    // The player currently occupying this tile, if any
    private Player player;
    // Our ClueGame screen
    protected ClueGame screen;

    public Tile(ClueGame screen) {
        this.screen = screen;
    }

    public void onEnter(Player player, boolean isPulled) {
        this.player = player;
        player.setTile(this);
        return;
    }

    public boolean canEnter() {
        return player == null;
    }

    public void onLeave() {
        setPlayer(null);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ArrayList<Tile> getMoves() {
        Tile[][] board = screen.getBoard();
        int y = 0;
        int x = 0;
        loop: for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++) {
                if(board[i][j].equals(this)) {
                    y = i;
                    x = j;
                    break loop;
                }
            }
        }

        ArrayList<Tile> moves = screen.getAdjacent(y, x);
        ArrayList<Tile> copy = (ArrayList<Tile>) moves.clone();
        for(Tile t : copy) {
            if(!t.canEnter()) {
                moves.remove(t);
            }
        }

        return moves;

    }

    public void clicked(Player p) {
        if(!canEnter()) {
            return;
        }
        p.getTile().onLeave();
        onEnter(p, false);
        return;
    }
}
