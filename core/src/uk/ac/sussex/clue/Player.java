package uk.ac.sussex.clue;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;

public class Player {

    // The colour of our token on the board
    private Color colour = Color.valueOf("FFFFFFFF");
    // The detective journal. Basically just a long string for us to render in a "book"
    private String notes;
    // Our arraylist of cards.
    private ArrayList<Card> cards;
    // Whether or not we're an AI. If we are, the programme will make our decisions. Otherwise, the player should.
    private boolean isAI = false;
    // The card which represents our character. Will be useful to be able to connect the two.
    private Card character;

    public Player(Card c, boolean isAI) {
        character = c;
        this.isAI = isAI;
        cards = new ArrayList<Card>();
    }

    public Color getColour() {
        return colour;
    }

    public Card getCharacter() {
        return character;
    }
}
