package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import java.util.ArrayList;
import java.util.Random;

/**
 * The player class.
 * Acts both as a representation of the player itself, holding carts and journals,
 * but also is the base of the AI.
 */
public class Player {

    // The colour of our token on the board
    private Color colour = Color.valueOf("FFFFFFFF");
    // The detective journal. Basically just a long string for us to render in a "book"
    private TextArea notes;
    // Our arraylist of cards.
    private ArrayList<Card> cards;
    // Whether or not we're an AI. If we are, the programme will make our decisions. Otherwise, the player should.
    private boolean isAI = false;
    // The card which represents our character. Will be useful to be able to connect the two.
    private Card character;
    // Tile we're int
    private Tile currentSpace;
    // Whether we've accused and got it wrong
    private boolean didAccuse = false;
    // Our screen
    private ClueGame screen;
    // our RNG
    private Random random = new Random();
    // Our list of cards we know aren't in the pack
    private ArrayList<Card> knownCards = new ArrayList<>();
    // Whether we guessed for 3 cards and got nothing back
    private boolean doWeKnow = false;
    // The room we're currently moving towards.
    private Room goal;

    /**
     * Our constructor.
     * Generates the journal, and initializes all the variables
     * @param c The card we're attached to
     * @param isAI Whether or not we're an AI
     * @param screen The screen we're a subset of
     */
    public Player(Card c, boolean isAI, ClueGame screen) {
        this.screen = screen;
        character = c;
        this.isAI = isAI;
        cards = new ArrayList<Card>();
        colour = c.getColour();

        // Initialize our notes
        notes = new TextArea("Write here", new Skin(Gdx.files.internal("data/skin.json")));
        notes.setPosition(560, 140);
        notes.setSize(800, 800);
    }

    /**
     * Return our colour
     * @return the colour that represents us
     */
    public Color getColour() {
        return colour;
    }

    /**
     * Return our card
     * @return the card we were created with
     */
    public Card getCharacter() {
        return character;
    }

    /**
     * Returns the tile we're standing in
     * @return The tile we're in
     */
    public Tile getTile() {
        return currentSpace;
    }

    /**
     *  Sets our tile to the given tile
     * @param t the tile to set it to
     */
    public void setTile(Tile t) {
        currentSpace = t;
    }

    /**
     * used to give a card to the player. Will add it to our list of known cards
     * @param c The card to give us
     */
    public void giveCard(Card c) {
        // Add it to our list of cards
        cards.add(c);
        // Add it to the list of cards we know aren't in the murder pack
        knownCards.add(c);
    }

    /**
     * @return Our list of cards
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * Sets whether or not we've accused and can't act anymore
     * @param didAccuse Whether or not we accused
     */
    public void setDidAccuse(boolean didAccuse) {
        this.didAccuse = didAccuse;
    }

    /**
     * @return Whether we have accused
     */
    public boolean isDidAccuse() {
        // Yes I know this method is terribly named, IntelliJ named it, okay.
        return didAccuse;
    }

    /**
     * @return Our notes
     */
    public TextArea getNotes() {
        return notes;
    }

    /**
     * @return Whether or not we're an AI
     */
    public boolean isAI() {
        return isAI;
    }

    /**
     * Picking a card from the given type of card
     *
     * Will randomly pick from any card of the given type that we don't know if exists
     * @param type The type we want to pick from
     * @return The card we've picked
     */
    public Card pickCard(Card.Types type) {
        ArrayList<Card> unknownCards = new ArrayList<>();

        // If we know which cards are in the pack
        if(doWeKnow) {
            // We just pick those cards
            for(Card c : knownCards) {
                if(c.isType(type)) {
                    return c;
                }
            }
        }

        // Otherwise, loop through all the cards and add all unknown cards to our list
        for(Card c : screen.getGameState().getCards().keySet()) {
            if(!knownCards.contains(c) && c.isType(type)) {
                unknownCards.add(c);
            }
        }

        // Then randomly pick a card from the list
        int randomlyChosen = random.nextInt(unknownCards.size());
        return unknownCards.get(randomlyChosen);
    }

    /**
     *  Chooses whether we should suggest or accuse
     * @return true if we accuse, false if we suggest
     */
    public boolean doWeAccuse() {
        // If we know all but 3 cards, we should accuse
        // Since the other 3 must be the last 3 cards
        if(knownCards.size() == cards.size()-3) {
            return true;
        }

        // If we know what the last 3 cards are and we're in a room
        if(doWeKnow && Room.class.isInstance(currentSpace)) {
            Room r = (Room) currentSpace;
            // And that room is the *right* room
            if(knownCards.contains(r.getCard())) {
                // We can accuse
                return true;
            }
        }
        // otherwise just suggest untill we get to the right room
        return false;
    }

    /**
     * Adds the given card to our list of known cards, if we've not already decided on 3 cards
     * @param c the card to add
     */
    public void knowCard(Card c) {
        if(!knownCards.contains(c) && !doWeKnow) {
            knownCards.add(c);
        }
    }

    /**
     * adds all the given cards to the known cards list
     *
     * Is called when we don't get any cards back from guessing, so we know we guessed rights
     * @param guesses The list of guesses to make known
     */
    public void weKnowTheCards(ArrayList<Card> guesses) {
        knownCards.clear();
        knownCards.addAll(guesses);
        doWeKnow = true;
    }

    /**
     * Called when we enter a room
     *
     * Resets our goal if we enter the room we're trying to get to
     */
    public void enteredRoom() {
        if(goal.equals(currentSpace)) {
            goal = null;
        }
    }

    /**
     * Our algorithm for picking a goal.
     *
     * Picks a room randomly from between the ones we aren't sure about
     */
    public void pickGoal() {
        // We already have a goal
        if(goal != null) {
            return;
        }

        ArrayList<Room> roomsToChooseFrom = new ArrayList<>();
        // Loop through all the rooms
        for(Room r : screen.getRooms()) {
            // If we have no clue about this room, or if we know it *is* this room, add it to the list
            if((!knownCards.contains(r.getCard()) && !doWeKnow) || (doWeKnow && knownCards.contains(r.getCard()))) {
                roomsToChooseFrom.add(r);
            }
        }

        // Then pick a random one from that list
        goal = roomsToChooseFrom.get(random.nextInt(roomsToChooseFrom.size()));
    }

    /**
     * @return Our current goal
     */
    public Room getGoal() {
        return goal;
    }
}
