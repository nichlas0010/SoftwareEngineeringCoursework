package uk.ac.sussex.clue;

import java.util.ArrayList;

/**
 * Doors are tiles, except you cannot step on them.
 * When you try, you either get moved in or out of the room it's connected to
 */
public class Door extends Tile {
    // the directions our room could be in
    enum directions {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    // The direction our room actually is in
    private directions dir;
    // x coordinate
    private int x;
    // y coordinate
    private int y;

    /**
     * Our constructor, just sets fields to their argument values
     * @param screen The ClueGame entity
     * @param dir the direction we'll be set to
     * @param x Our x coordinate from the top left
     * @param y Our y coordinate from the top left
     */
    public Door(ClueGame screen, directions dir, int x, int y) {
        super(screen);
        this.dir = dir;
        this.x = x;
        this.y = y;
    }

    /**
     * Called when a player enters the door. Just moves them into the room we're connected to
     * @param player The player that entered the tile
     * @param isPulled Whether or not they were pulled in here
     */
    @Override
    public void onEnter(Player player, boolean isPulled) {
        getRoom().onEnter(player, false);
    }

    /**
     * Gets the room that's in the direction we're pointing
     * @return The room in our direction
     */
    public Tile getRoom() {
        switch(dir) {
            case UP:
                return screen.getBoard()[y-1][x];
            case DOWN:
                return screen.getBoard()[y+1][x];
            case LEFT:
                return screen.getBoard()[y][x-1];
            case RIGHT:
                return screen.getBoard()[y][x+1];
        }
        return null;
    }

    /**
     * Gets the tile opposite of the room, IE the tile right outside the door
     * @return A tile outside the door
     */
    public Tile getOpposite() {
        switch(dir) {
            case UP:
                return screen.getBoard()[y+1][x];
            case DOWN:
                return screen.getBoard()[y-1][x];
            case LEFT:
                return screen.getBoard()[y][x+1];
            case RIGHT:
                return screen.getBoard()[y][x-1];
        }
        return null;
    }

    /**
     *  Called when this door is clicked on. If the tile our player is on is opposite of the room, they can enter
     *  Otherwise they can leave
     * @param p the Player that clicked on our tile. Will usually be the current player
     */
    @Override
    public void clicked(Player p) {
        // Are you outside the door? Can you move?
        if(!p.getTile().equals(getOpposite()) || !screen.getGameState().canMove()) {
            return;
        }
        // If you are, you get to enter the door
        p.getTile().onLeave();
        onEnter(p, false);
        return;
    }

    /**
     * Gets our list of moves.
     * Just the tile outside the door, and the room
     * @return An arraylist containing the Tile and the Room
     */
    @Override
    public ArrayList<Tile> getMoves() {
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(getOpposite());
        tiles.add(getRoom());
        return tiles;
    }
}
