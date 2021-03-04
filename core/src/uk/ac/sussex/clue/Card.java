package uk.ac.sussex.clue;
import com.badlogic.gdx.Gdx;
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

    public Card(Weapons w) {
        this(w.name().toLowerCase());
        type = Types.WEAPON;
    }

    public Card(Characters c) {
        this(c.name().toLowerCase());
        type = Types.CHARACTER;
    }

    public Card(Rooms r) {
        this(r.name().toLowerCase());
        type = Types.ROOM;
    }

    public Card(String s) {
        card = s;
        image = new Texture(Gdx.files.internal(s+".png"));
        name = Gdx.files.internal(s+".txt").readString();
    }

    public boolean isType(Types t) {
        return t.equals(type);
    }

    public String getCard() {
        return card;
    }
}
