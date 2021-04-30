package uk.ac.sussex.clue;

import java.util.ArrayList;

/**
 * The rooms that we can enter and accuse from
 *
 * Extends tile and works almost identically to tiles, except they're more picky about how they pick their moves, and cover multiple tiles
 */
public class Room extends Tile {
    // List of all doors connected to us
    private ArrayList<Door> doors = new ArrayList();
    // List of all rooms we're connected to via secret passage
    private ArrayList<Room> passages = new ArrayList();
    // Associated card
    private Card card;
    // The card's value
    private Card.Rooms roomtype;

    /**
     *
     * @param screen The ClueGame entity we're a subset of
     * @param c The card we're based on
     */
    public Room(ClueGame screen, Card c) {
        super(screen);
        card = c;
        roomtype = c.getRoomtype();
    }

    /**
     *  Gets all the moves we can make.
     *  Starts off by getting the tiles outside of all doors, then any rooms we're connected to by passages
     * @return ArrayList of tiles you can move to from here
     */
    @Override
    public ArrayList<Tile> getMoves() {
        ArrayList<Tile> moves = new ArrayList<Tile>();
        for(Door d : doors) {
            Tile t = d.getOpposite();
            if(t.canEnter()) {
                moves.add(t);
            }
        }
        for(Tile t : passages) {
            moves.add(t);
        }
        return moves;
    }

    /**
     * We don't allow entering the room directly, only through doors
     * @return false
     */
    @Override
    public boolean canEnter() {
        return false;
    }

    /**
     * Called when a player enters the room
     *
     * If they weren't pulled, they get to choose whether to guess or accuse
     *
     * If they're an AI, we call {@link Player#enteredRoom()} to determine what to do
     *
     * @param player The player that entered the tile
     * @param isPulled Whether or not they were pulled in here
     */
    @Override
    public void onEnter(Player player, boolean isPulled) {
        player.setTile(this);
        if(isPulled) {
            return;
        }

        // Call the AI method
        if(player.isAI()) {
            player.enteredRoom();
        }

        // Set us to the right gamestate
        screen.getGameState().setState(GameState.State.CHOICE);
    }

    /**
     * Gets the roomtype we're attached to
     * @return Our room type
     */
    public Card.Rooms getRoomtype() {
        return roomtype;
    }

    /**
     * Adds another room to our passages, so we're connected to it
     * @param r The room we should connect to
     */
    public void addPassage(Room r) {
        // Don't bother if it's already there
        if(!passages.contains(r)) {
            passages.add(r);
        }
    }

    /**
     * Adds a door to our room that you can enter/exit from
     * @param d The Door tile to add
     */
    public void addDoor(Door d) {
        // Don't bother if it's already there
        if(!doors.contains(d)) {
            doors.add(d);
        }
    }

    /**
     * Similar to our inherited method, except we check whether the tile is next to us rather than whether they can enter
     * @param p the Player that clicked on our tile. Will usually be the current player
     */
    @Override
    public void clicked(Player p) {
        if(!getMoves().contains(p.getTile()) || !screen.getGameState().canMove()) {
            return;
        }
        p.getTile().onLeave();
        onEnter(p, false);
    }

    /**
     * gets our card
     * @return the card
     */
    public Card getCard() {
        return card;
    }
}
