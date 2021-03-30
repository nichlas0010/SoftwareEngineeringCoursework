package uk.ac.sussex.clue;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Card {

    enum Types {
        WEAPON, CHARACTER, ROOM
    }

    enum Weapons {
        CANDLESTICK, DAGGER, PIPE, REVOLVER, ROPE, WRENCH
    }

    enum Characters {
        WHITE, GREEN, PEACOCK, PLUM, SCARLET, MUSTARD
    }

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

    public Card(Weapons w) {
        this(w.name().toLowerCase());
        type = Types.WEAPON;
    }

    public Card(Characters c) {
        this(c.name().toLowerCase());
        type = Types.CHARACTER;
        colour = Color.valueOf(Gdx.files.internal(c.name().toLowerCase()+".txt").readString().split("\n")[1]);
    }

    public Card(Rooms r) {
        this(r.name().toLowerCase());
        type = Types.ROOM;
        roomtype = r;
    }

    public Card(String s) {
        card = s;
        image = new Texture(Gdx.files.internal(s+".png"));
        name = Gdx.files.internal(s+".txt").readString().split("\n")[0];
    }

    public boolean isType(Types t) {
        return t.equals(type);
    }

    public String getCard() {
        return card;
    }

    public Rooms getRoomtype() {
        return roomtype;
    }

    public Texture getImage() {
        return image;
    }

    public Color getColour() {
        return colour;
    }
}
