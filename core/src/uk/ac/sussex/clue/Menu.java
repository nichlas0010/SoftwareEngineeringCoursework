package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(MainController.instance.getCamera().combined);


        batch.begin();
        if(isOptions) {
            fullscreen.setText(Gdx.graphics.isFullscreen()?"Fullscreen: On":"Fullscreen: Off");
            fullscreen.draw(batch, 1);
            exitOptions.draw(batch, 1);
            font.draw(batch, "Audio volume: " + MainController.instance.getVolume(), 775, 525);
            audioSlider.draw(batch, 1);
            audioSlider.setDisabled(false);
            MainController.instance.setVolume((int) audioSlider.getValue());
        } else {
            newGame.draw(batch, 1);
            options.draw(batch, 1);
            exitGame.draw(batch, 1);
            audioSlider.setDisabled(true);
        }
        batch.end();
    }

    @Override
    public void show () {
        super.show();

        // Create our textbutton style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.font.setColor(Color.WHITE);

        // Create our menu buttons
        newGame = new TextButton("New Game", style);
        newGame.setPosition(960-newGame.getWidth()/2, 530);

        options = new TextButton("Options", style);
        options.setPosition(960-options.getWidth()/2, 470);

        exitGame = new TextButton("Exit Game", style);
        exitGame.setPosition(960-exitGame.getWidth()/2, 410);

        fullscreen = new TextButton("Fullscreen: Off", style);
        fullscreen.setPosition(775, 540);

        exitOptions = new TextButton("Back", style);
        exitOptions.setPosition(775, 425);

        audioSlider = new Slider(0, 100, 1, false, new Skin(Gdx.files.internal("data/skin.json")));
        audioSlider.setPosition(775, 475);
        audioSlider.setValue((float) MainController.instance.getVolume());

        exitGame.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(!isOptions) {
                    System.exit(0);
                }
            }
        });

        options.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(!isOptions) {
                   isOptions = true;
                }
            }
        });

        newGame.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(!isOptions) {
                    MainController.instance.setScreen(new NewGame());
                }
            }
        });

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

        exitOptions.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(isOptions) {
                    isOptions = false;
                }
            }
        });


        addActor(exitGame);
        addActor(options);
        addActor(newGame);
        addActor(fullscreen);
        addActor(audioSlider);
        addActor(exitOptions);
    }

    @Override
    public void hide() {
        super.hide();
        exitGame.remove();
        options.remove();
        newGame.remove();
        fullscreen.remove();
        audioSlider.remove();
        exitOptions.remove();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }

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
