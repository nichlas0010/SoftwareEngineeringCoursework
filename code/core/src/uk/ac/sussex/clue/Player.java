package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

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

    public Player(Card c, boolean isAI, ClueGame screen) {
        this.screen = screen;
        character = c;
        this.isAI = isAI;
        cards = new ArrayList<Card>();
        colour = c.getColour();
        notes = new TextArea("Write here", new Skin(Gdx.files.internal("data/skin.json")));
        notes.setPosition(560, 140);
        notes.setSize(800, 800);
    }

    public Color getColour() {
        return colour;
    }

    public Card getCharacter() {
        return character;
    }

    public Tile getTile() {
        return currentSpace;
    }

    public void setTile(Tile t) {
        currentSpace = t;
    }

    public void giveCard(Card c) {
        cards.add(c);
        knownCards.add(c);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setDidAccuse(boolean didAccuse) {
        this.didAccuse = didAccuse;
    }

    public boolean isDidAccuse() {
        return didAccuse;
    }

    public TextArea getNotes() {
        return notes;
    }

    public boolean isAI() {
        return isAI;
    }

    public Card pickCard(Card.Types type) {
        ArrayList<Card> unknownCards = new ArrayList<>();

        if(doWeKnow) {
            for(Card c : knownCards) {
                if(c.isType(type)) {
                    return c;
                }
            }
        }

        for(Card c : screen.getGameState().getCards().keySet()) {
            if(!knownCards.contains(c) && c.isType(type)) {
                unknownCards.add(c);
            }
        }
        int randomlyChosen = random.nextInt(unknownCards.size());
        return unknownCards.get(randomlyChosen);
    }

    public boolean doWeAccuse() {
        if(knownCards.size() == cards.size()-3) {
            return true;
        }
        if(doWeKnow && Room.class.isInstance(currentSpace)) {
            Room r = (Room) currentSpace;
            if(knownCards.contains(r.getCard())) {
                return true;
            }
        }
        return false;
    }

    public void knowCard(Card c) {
        if(!knownCards.contains(c) && !doWeKnow) {
            knownCards.add(c);
        }
    }

    public void weKnowTheCards(ArrayList<Card> guesses) {
        knownCards.clear();
        knownCards.addAll(guesses);
        doWeKnow = true;
    }

    public void enteredRoom() {
        if(goal.equals(currentSpace)) {
            goal = null;
        }
    }

    public void pickGoal() {
        if(goal != null) {
            return;
        }

        ArrayList<Room> roomsToChooseFrom = new ArrayList<>();
        for(Room r : screen.getRooms()) {
            if((!knownCards.contains(r.getCard()) && !doWeKnow) || (doWeKnow && knownCards.contains(r.getCard()))) {
                roomsToChooseFrom.add(r);
            }
        }

        goal = roomsToChooseFrom.get(random.nextInt(roomsToChooseFrom.size()));
    }

    public Room getGoal() {
        return goal;
    }
}
