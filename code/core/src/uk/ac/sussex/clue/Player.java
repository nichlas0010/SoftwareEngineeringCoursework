package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.util.ArrayList;

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

    public Player(Card c, boolean isAI) {
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
}
