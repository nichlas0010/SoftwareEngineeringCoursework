package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * The main menu for our game.
 * Will have a bunch of buttons
 */
public class Menu extends Window {

    // Whether or not we're in the options menu
    private boolean isOptions = false;
    // Our newgame button
    private TextButton newGame;
    // Our options button
    private TextButton options;
    // our exit game button
    private TextButton exitGame;
    // our fullscreen button
    private TextButton fullscreen;
    // our exit options button
    private TextButton exitOptions;
    // the slider for the audio
    private Slider audioSlider;
    // our Background Image
    private Image backgroundImage = new Image(new Texture(Gdx.files.internal("menuBackground.png")));

    /**
     * Renders every frame
     * @param delta
     */
    @Override
    public void render (float delta) {
        // Clear our screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(MainController.instance.getCamera().combined);


        batch.begin();
        // Draw the background first
        backgroundImage.draw(batch, 1);

        // If we're in the options menu
        if(isOptions) {
            // Draw the fullscreen button
            fullscreen.setText(Gdx.graphics.isFullscreen()?"Fullscreen: On":"Fullscreen: Off");
            fullscreen.draw(batch, 1);
            // and the exit options button
            exitOptions.draw(batch, 1);
            // Then our audio slider which isn't used since we never added audio
            font.draw(batch, "Audio volume: " + MainController.instance.getVolume(), 775, 525);
            audioSlider.draw(batch, 1);
            audioSlider.setDisabled(false);
            // Set the mainController's volume to the audioslider's volume
            MainController.instance.setVolume((int) audioSlider.getValue());

        // If we're not in the options menu
        } else {
            // Draw our 3 buttons
            newGame.draw(batch, 1);
            options.draw(batch, 1);
            exitGame.draw(batch, 1);
            // Disable the slider
            audioSlider.setDisabled(true);
        }
        batch.end();
    }

    /**
     *  Functions as a pseudo constructor
     *  Creates our images and buttons
     */
    @Override
    public void show () {
        super.show();

        // Create our textbutton style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.font.setColor(Color.WHITE);

        // Create our background image
        backgroundImage.setPosition(0, 0);
        backgroundImage.setSize(1920, 1080);

        // Create our new game button
        newGame = new TextButton("New Game", style);
        newGame.setPosition(960-newGame.getWidth()/2, 530);

        // Create our options button
        options = new TextButton("Options", style);
        options.setPosition(960-options.getWidth()/2, 470);

        // Create our exit button
        exitGame = new TextButton("Exit Game", style);
        exitGame.setPosition(960-exitGame.getWidth()/2, 410);

        // Create the fullscreen button
        fullscreen = new TextButton("Fullscreen: Off", style);
        fullscreen.setPosition(775, 540);

        // Create the button to exit the options menu
        exitOptions = new TextButton("Back", style);
        exitOptions.setPosition(775, 425);

        // Create our audioslider
        audioSlider = new Slider(0, 100, 1, false, new Skin(Gdx.files.internal("data/skin.json")));
        audioSlider.setPosition(775, 475);
        audioSlider.setValue((float) MainController.instance.getVolume());

        // Then we add all our listeners

        // This one exits the game
        exitGame.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(!isOptions) {
                    System.exit(0);
                }
            }
        });

        // This one enters the options menu
        options.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(!isOptions) {
                   isOptions = true;
                }
            }
        });

        // This one goes to the character selection screen
        newGame.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(!isOptions) {
                    MainController.instance.setScreen(new NewGame());
                }
            }
        });

        // This one toggles fullscreen
        fullscreen.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(isOptions) {
                    if(Gdx.graphics.isFullscreen()) {
                        Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    } else {
                        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    }
                }
            }
        });

        // This one exits the options menu
        exitOptions.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(isOptions) {
                    isOptions = false;
                }
            }
        });


        // Then add all the buttons to the stage so they're clickable
        addActor(exitGame);
        addActor(options);
        addActor(newGame);
        addActor(fullscreen);
        addActor(audioSlider);
        addActor(exitOptions);
    }

    /**
     * This method is called when the menu is closed.
     * Removes all the buttons, essentially just garbage collects
     */
    @Override
    public void hide() {
        exitGame.remove();
        options.remove();
        newGame.remove();
        fullscreen.remove();
        audioSlider.remove();
        exitOptions.remove();
        super.hide();
    }

    /**
     * This method is called whenever a button is pressed, and quits if the escape button is pressed
     * @param keycode the key that is pressed, @see {@link com.badlogic.gdx.Input.Keys}
     */
    @Override
    public void keyDown(int keycode) {
        if(keycode == Input.Keys.ESCAPE) {
            if (isOptions) {
                isOptions = false;
            } else {
                System.exit(0);
            }
        }
        return;
    }

}
