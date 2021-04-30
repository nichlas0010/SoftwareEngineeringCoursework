package uk.ac.sussex.clue;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * Our cards. These cards represent every card you'd have in the game, including weapons, characters, and rooms
 *
 * This class will have everything on here, including its name, and image
 */
public class Card {

    // The different types of card
    enum Types {
        WEAPON, CHARACTER, ROOM
    }

    // The different weapon types
    enum Weapons {
        CANDLESTICK, DAGGER, PIPE, REVOLVER, ROPE, WRENCH
    }

    // The different character types
    enum Characters {
        WHITE, GREEN, PEACOCK, PLUM, SCARLET, MUSTARD
    }

    // The different room types
    enum Rooms {
        KITCHEN, BALLROOM, CONSERVATORY, BILLIARDS, LIBRARY, STUDY, HALL, LOUNGE, DINING
    }

    // The texture of our image. The path will be interpreted in the constructor
    private Texture image;
    // The name of our character. Will be drawn from the txt file defining it
    private String name;
    // the type of card we are
    private Types type;
    // Which specific card we are
    private String card;

    // If we're a character, what colour we are
    private Color colour;
    // If we're a room, and which one we are
    private Rooms roomtype;
    // If we're a character, which character we are
    private Characters character;

    /**
     * Constructor for weapons. Sets the type to weapon, then calls {@link #Card(String)}
     * @param w The weapon type we represent
     */
    public Card(Weapons w) {
        this(w.name().toLowerCase());
        type = Types.WEAPON;
    }

    /**
     * Constructor for characters. Sets the type to character, seperates the colour from the character, then calls {@link #Card(String)}
     * @param c the character we represent
     */
    public Card(Characters c) {
        this(c.name().toLowerCase());
        type = Types.CHARACTER;
        // We need to get the colour for our player chip
        colour = Color.valueOf(Gdx.files.internal(c.name().toLowerCase()+".txt").readString().split("\n")[1]);
        character = c;
    }

    /**
     * Constructor for rooms. Sets the type to room, then calls {@link #Card(String)}
     * @param r the room type we represent
     */
    public Card(Rooms r) {
        this(r.name().toLowerCase());
        type = Types.ROOM;
        roomtype = r;
    }

    /**
     * Constructor for the card. Sets our name to the value given
     * @param s The name of our card
     */
    public Card(String s) {
        card = s;
        // Get our texture from the file
        image = new Texture(Gdx.files.internal(s+".png"));
        // Then get our name
        name = Gdx.files.internal(s+".txt").readString().split("\n")[0];
    }

    /**
     * Checks whether or not *we* are of the type provided
     * @param t The type we compare ourself to
     * @return True if we're the same type, false otherwise
     */
    public boolean isType(Types t) {
        return t.equals(type);
    }

    /**
     * @return Gets the filepath of the card, minus any extensions
     */
    public String getCard() {
        return card;
    }

    /**
     * @return The type of room we are
     */
    public Rooms getRoomtype() {
        return roomtype;
    }

    /**
     * @return The texture of our card
     */
    public Texture getImage() {
        return image;
    }

    /**
     * @return The colour of our chip on the board
     */
    public Color getColour() {
        return colour;
    }

    /**
     * @return Gets the name of the card
     */
    public String getName() {
        return name;
    }

    /**
     * @return Gets the character we represent, assuming we represent one
     */
    public Characters getCharacter() {
        return character;
    }
}
