package uk.ac.sussex.clue;

import java.util.ArrayList;

public class Room extends Tile {
    // List of all doors connected to us
    private ArrayList<Door> doors = new ArrayList();
    // List of all rooms we're connected to via secret passage
    private ArrayList<Room> passages = new ArrayList();
    // Associated card
    private Card card;
    // The card value
    private Card.Rooms roomtype;

    public Room(ClueGame screen, Card c) {
        super(screen);
        card = c;
        roomtype = c.getRoomtype();
    }

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

    @Override
    public boolean canEnter() {
        return false;
    }

    public void onEnter(Player player, boolean isPulled) {
        player.setTile(this);
        if(isPulled) {
            return;
        }
        // TODO: make the player do a thing
    }

    public Card.Rooms getRoomtype() {
        return roomtype;
    }

    public void addPassage(Room r) {
        if(!passages.contains(r)) {
            passages.add(r);
        }
    }

    public void addDoor(Door d) {
        if(!doors.contains(d)) {
            doors.add(d);
        }
    }
}
