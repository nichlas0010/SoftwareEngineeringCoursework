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
import sun.rmi.rmic.Main;

import java.util.ArrayList;
import java.util.HashMap;

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


    public NewGame() {
        int x = 0;

        backgroundImage.setPosition(0, 0);
        backgroundImage.setSize(1920, 1080);

        Skin s = new Skin(Gdx.files.internal("data/skin.json"));

        for(Card.Characters c : Card.Characters.values()) {
            Card card = new Card(c);
            Image i = new Image(card.getImage());
            images.put(card, i);

            i.setSize(192, 256);
            i.setPosition(100+(256*x), 540);

            final CheckBox player = new CheckBox("Player", s);
            final CheckBox computer = new CheckBox("Computer", s);
            final CheckBox absent = new CheckBox("None", s);

            boxes.put(card, new ArrayList<CheckBox>());
            boxes.get(card).add(player);
            boxes.get(card).add(computer);
            boxes.get(card).add(absent);

            player.setPosition(124+(256*x), 500);
            computer.setPosition(124+(256*x), 480);
            absent.setPosition(124+(256*x), 460);

            absent.setChecked(true);

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

            addActor(player);
            addActor(computer);
            addActor(absent);


            x++;
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

        beginButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                String config = "";
                for(Card c : boxes.keySet()) {
                    if(boxes.get(c).get(2).isChecked()) {
                        continue;
                    }

                    if(config.length() > 0) {
                        config += ";";
                    }

                    config += getSymbolFromCharacter(c.getCharacter());
                    if(boxes.get(c).get(1).isChecked()) {
                        config += "c";
                    } else {
                        config += "p";
                    }
                }

                if(config.length() < 5) {
                    playerCountAlert = true;
                } else {
                    MainController.instance.setScreen(new ClueGame(config));
                }
            }
        });

        returnButton.addListener(new ClickListener() {
           public void clicked(InputEvent event, float x, float y) {
               MainController.instance.setScreen(new Menu());
           }
        });

        addActor(returnButton);
        addActor(beginButton);

    }

    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(MainController.instance.getCamera().combined);
        batch.begin();

        backgroundImage.draw(batch, 1);

        for(Card c : images.keySet()) {
            images.get(c).draw(batch, 50);
            boxes.get(c).get(0).draw(batch, 50);
            boxes.get(c).get(1).draw(batch, 50);
            boxes.get(c).get(2).draw(batch, 50);
        }

        beginButton.draw(batch, 50);
        returnButton.draw(batch, 50);

        if(playerCountAlert) {
            font.draw(batch, "The game needs at least 2 participants!", 750, 100);
        }

        batch.end();

    }

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
