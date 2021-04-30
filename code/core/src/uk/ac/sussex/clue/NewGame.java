package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Our NewGame menu
 *
 * Is the character selection screen, where we choose which characters we want, and whether they're AIs or players
 */
public class NewGame extends Window {
    //List of cards and their images
    private HashMap<Card, Image> images = new HashMap<>();
    //List of cards and their checkboxes
    private HashMap<Card, ArrayList<CheckBox>> boxes = new HashMap<>();
    // Our begin button
    private TextButton beginButton;
    // Our menu button
    private TextButton returnButton;
    // Our background
    private Image backgroundImage = new Image(new Texture(Gdx.files.internal("newGameBackground.png")));
    // Whether they tried to add too few players, tell them they need at least to if it's true
    private boolean playerCountAlert = false;


    /**
     * Our constructor sets up the menu, with all of the images and buttons being created
     */
    public NewGame() {

        // Which portrait we're dealing with
        int portraitNo = 0;

        // Create our background image first
        backgroundImage.setPosition(0, 0);
        backgroundImage.setSize(1920, 1080);

        // Then create our skin which will be used for our checkboxes
        Skin s = new Skin(Gdx.files.internal("data/skin.json"));

        // Then we loop through all the possible characters
        for(Card.Characters c : Card.Characters.values()) {

            // Create a new card for us to use
            Card card = new Card(c);
            // Then the image using the card's texture
            Image i = new Image(card.getImage());
            images.put(card, i);

            // Set it's proportions and position
            i.setSize(192, 256);
            // We derive its position from the portraitNo.
            i.setPosition(100+(256*portraitNo), 540);

            // Create our 3 checkboxes
            // Player sets this character to be a player
            final CheckBox player = new CheckBox("Player", s);
            // Computer sets this character to be a player
            final CheckBox computer = new CheckBox("Computer", s);
            // Absent sets this character to be, well, absent
            final CheckBox absent = new CheckBox("None", s);

            // Add them all to a new arrayList in the boxes hashmap
            boxes.put(card, new ArrayList<CheckBox>());
            boxes.get(card).add(player);
            boxes.get(card).add(computer);
            boxes.get(card).add(absent);

            // Set their positions, derived from portraitNo
            player.setPosition(124+(256*portraitNo), 500);
            computer.setPosition(124+(256*portraitNo), 480);
            absent.setPosition(124+(256*portraitNo), 460);

            // the absent box is checked by default
            absent.setChecked(true);

            // Then we create our listeners
            // Each listener sets the box it's attached to to true, and the others to false
            player.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    computer.setChecked(false);
                    absent.setChecked(false);
                    player.setChecked(true);
                }
            });

            computer.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    player.setChecked(false);
                    computer.setChecked(true);
                    absent.setChecked(false);
                }
            });

            absent.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    player.setChecked(false);
                    computer.setChecked(false);
                    absent.setChecked(true);
                }
            });

            // Then we make them actually clickable
            addActor(player);
            addActor(computer);
            addActor(absent);

            // and iterate
            portraitNo++;
        }

        // Create our textbutton style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.font.setColor(Color.WHITE);

        // Create our begin button
        beginButton = new TextButton("Begin", style);
        beginButton.setPosition((1920/2)-(beginButton.getWidth()/2), 200);

        // and our return to menu button
        returnButton = new TextButton("Return", style);
        returnButton.setPosition((1920/2)-(returnButton.getWidth()/2), 150);

        // Then add the listener to the beginbutton
        beginButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                // Prepare the config for our ClueGame
                String config = "";
                for(Card c : boxes.keySet()) {
                    // If the absent box is ticked, continue to the next box
                    if(boxes.get(c).get(2).isChecked()) {
                        continue;
                    }

                    // If this isn't the first config entry, add a seperator
                    if(config.length() > 0) {
                        config += ";";
                    }

                    // Add the first character representing our character
                    config += getSymbolFromCharacter(c.getCharacter());
                    // If it's a computer, add a c
                    if(boxes.get(c).get(1).isChecked()) {
                        config += "c";
                    // Otherwise, add a p
                    } else {
                        config += "p";
                    }
                }

                // If there's not at least 2 players, alert the user that they need 2 to continue
                if(config.length() < 5) {
                    playerCountAlert = true;
                // Otherwise, start a new game using the config
                } else {
                    MainController.instance.setScreen(new ClueGame(config));
                }
            }
        });

        // Add our return button listener
        returnButton.addListener(new ClickListener() {
           public void clicked(InputEvent event, float x, float y) {
               // Just return us to the menu
               MainController.instance.setScreen(new Menu());
           }
        });

        // Add both actors to the stage, so they're clickable
        addActor(returnButton);
        addActor(beginButton);

    }

    /**
     * This method renders every frame
     * @param delta
     */
    @Override
    public void render(float delta) {

        // Clear the previous frame
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set the right projection, then begin the batch
        batch.setProjectionMatrix(MainController.instance.getCamera().combined);
        batch.begin();

        // Draw the background first so everything else draws on top
        backgroundImage.draw(batch, 1);


        // Draw all our images and their checkboxes
        for(Card c : images.keySet()) {
            font.draw(batch, c.getName(), images.get(c).getX(), 850);
            images.get(c).draw(batch, 50);
            boxes.get(c).get(0).draw(batch, 50);
            boxes.get(c).get(1).draw(batch, 50);
            boxes.get(c).get(2).draw(batch, 50);
        }

        // Then draw our two buttons
        beginButton.draw(batch, 50);
        returnButton.draw(batch, 50);

        // If we have an alert, draw that
        if(playerCountAlert) {
            font.draw(batch, "The game needs at least 2 participants!", 750, 100);
        }

        // then stop
        batch.end();

    }

    /**
     * Returns one character which is the representation of the given character
     * 
     * @see {@link ClueGame#getCharacterFromSymbol(String)}
     * @param c the character to convert
     * @return a string with one letter, representing the character
     */
    public String getSymbolFromCharacter(Card.Characters c) {
        switch(c) {
            case WHITE:
                return "w";
            case GREEN:
                return "g";
            case PEACOCK:
                return "e";
            case PLUM:
                return "l";
            case SCARLET:
                return "s";
            case MUSTARD:
                return "m";
        }
        return "";
    }
}
