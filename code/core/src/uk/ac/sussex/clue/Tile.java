package uk.ac.sussex.clue;
import java.util.ArrayList;

/**
 * Tiles as used in the game of Clue! Rooms and Doors inherit from this.
 *
 * Tiles are spaces for the players to stand in, and to trigger effects when entered
 */
public class Tile {
    // The player currently occupying this tile, if any
    private Player player;
    // Our ClueGame screen
    protected ClueGame screen;
    // A list of moves the AI can move from here. This list is temporary and is changed every time the AI needs to move
    private ArrayList<Tile> aiMoves = new ArrayList<>();

    /**
     * @param screen The ClueGame that created us
     */
    public Tile(ClueGame screen) {
        this.screen = screen;
    }

    /**
     * This method is called whenever this tile is entered.
     * If the player wasn't pulled in here, will call {@link ClueGame#isInRoom()}
     * @param player The player that entered the tile
     * @param isPulled Whether or not they were pulled in here
     */
    public void onEnter(Player player, boolean isPulled) {
        setPlayer(player);
        // Update the player's tile
        player.setTile(this);
        // if they weren't pulled, check if they're done moving
        if(!isPulled) {
            screen.isInRoom();
        }
    }

    /**
     * Whether or not a player is allowed to enter this tile.
     * @return True if there's no player here, false if there is
     */
    public boolean canEnter() {
        return player == null;
    }

    /**
     * Called whenever a player leaves this tile. Updates the player variable to null
     */
    public void onLeave() {
        setPlayer(null);
    }

    /**
     * Sets the player variable to the player provided
     * @param player the player who entered
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets a list of all the tiles that we can move to from this tile
     * If we're next to a door it returns the room instead of the door
     *
     * @return an ArrayList of Tile, that we can move to from this tile
     */
    public ArrayList<Tile> getMoves() {
        Tile[][] board = screen.getBoard();
        int y = 0;
        int x = 0;

        // Loop through the entire board
        loop: for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++) {
                // Till we find the co-ordinates for the current tile
                if(board[i][j].equals(this)) {
                    y = i;
                    x = j;
                    break loop;
                }
            }
        }

        // Then get, the adjacent tiles from our ClueGame
        ArrayList<Tile> moves = screen.getAdjacent(y, x);
        ArrayList<Tile> copy = (ArrayList<Tile>) moves.clone();
        // Loop through all our adjacent tiles
        for(Tile t : copy) {
            // If we can't enter it, remove it
            if(!t.canEnter()) {
                moves.remove(t);
            }

            // If it's a door, and we're not the tile that can access it, remove the door
            if(Door.class.isInstance(t)) {
                if(!t.getMoves().contains(this)) {
                    moves.remove(t);
                }
            }
        }

        // Return our list of moves
        return moves;

    }

    /**
     * Called by the ClueGame when the user clicks on a tile
     * @param p the Player that clicked on our tile. Will usually be the current player
     */
    public void clicked(Player p) {
        // If they can't enter or they can't move, just do nothing
        if(!canEnter() || !screen.getGameState().canMove()) {
            return;
        }
        // Otherwise, they leave the tile they were just in
        p.getTile().onLeave();
        // And enter this one
        onEnter(p, false);
    }

    /**
     * Used by subclasses {@link Rollagain#reset()} and {@link Freesuggestion#reset()}
     */
    public void reset() {
        return;
    }

    /**
     * Clears our AI moves. Called when we path out which moves the AI can take
     */
    public void resetAIMoves() {
        aiMoves.clear();
    }

    /**
     * Adds a tile to our possible moves
     * @param t The tile we can now move to
     */
    public void addAIMove(Tile t) {
        aiMoves.add(t);
    }

    /**
     * Gets the list of Tiles we can move to
     * @return ArrayList of tiles the AI will path to from here
     */
    public ArrayList<Tile> getAiMoves() {
        return aiMoves;
    }
}
