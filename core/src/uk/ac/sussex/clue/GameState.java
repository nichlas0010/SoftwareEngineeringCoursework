package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameState {
    // Our game
    private ClueGame screen;
    // amount of moves left
    private int moves = 0;
    // has rolled
    private boolean hasRolled = false;
    // dice
    private int[] dice = new int[2];
    // dice countdown
    private int diceCountdown;
    // Die 1
    private Image die1;
    // Die 2
    private Image die2;
    // Guess and accusation images
    private Image guess = new Image(new Texture(Gdx.files.internal("guess.png")));
    private Image accuse = new Image(new Texture(Gdx.files.internal("accuse.png")));
    // List of dice textures
    private ArrayList<SpriteDrawable> diceTextures = new ArrayList<>();
    // True is accuse, false is guess
    private boolean isAccusing = false;
    // Which states we can be in
    enum State {
        ROLLING, MOVING, CHOICE, GUESSCHAR, GUESSWEAPON, GUESSROOM, DONEGUESSING, BLOCKED, GIVECARD, SEECARD, SEEMURDER
    }
    // which state we're actually in
    private State state;
    // RNG
    private Random random = new Random();
    // Our cards
    private final HashMap<Card, Image> cards = new HashMap<>();
    // The guessed cards
    private ArrayList<Card> guesses = new ArrayList<>();



    public GameState(ClueGame screen) {
        this.screen = screen;

        setupDice();

        setupGuessAccuse();

    }


    public void render() {
        Batch batch = screen.getBatch();
        switch(state) {
            case ROLLING:
                renderDice(batch);
                break;
            case MOVING:
                renderMoves(batch);
                break;
            case CHOICE:
                renderChoice(batch);
                break;
            case GUESSCHAR:
                guesses.clear();
                renderGuessChar(batch);
                break;
            case GUESSWEAPON:
                renderGuessWeapon(batch);
                break;
        }

    }

    public boolean canMove() {
        if(state.equals(State.MOVING) && moves > 0) {
            moves--;
            return true;
        }
        return false;
    }

    public int getMoves() {
        return moves;
    }

    public void setState(State state) {
        this.state = state;
        switch(state) {
            case ROLLING:
                screen.nextPlayer();
                hasRolled = false;
                screen.addActor(die1);
                screen.addActor(die2);
                break;
            case MOVING:
                die1.remove();
                die2.remove();
                moves = dice[0]+dice[1];
                break;
            case CHOICE:
                screen.addActor(guess);
                screen.addActor(accuse);
                break;
            case GUESSCHAR:
                guess.remove();
                accuse.remove();
                for(Card c : cards.keySet()) {
                    if(c.isType(Card.Types.CHARACTER)) {
                        screen.addActor(cards.get(c));
                    }
                }
                break;
            case GUESSWEAPON:
                for(Card c : cards.keySet()) {
                    if(c.isType(Card.Types.WEAPON)) {
                        screen.addActor(cards.get(c));
                    } else
                        cards.get(c).remove();
                }
                break;
            case GUESSROOM:
                for(Card c : cards.keySet()) {
                    cards.get(c).remove();
                }
                Room r = (Room) screen.getCurrentPlayer().getTile();
                guesses.add(r.getCard());
                setState(State.DONEGUESSING);
                break;
            case DONEGUESSING:
                screen.movePlayer(guesses.get(0));
                setState(State.ROLLING);
                break;
        }
    }

    public void renderDice(Batch batch) {
        if(!hasRolled) {
            diceCountdown -= 1;
            if(diceCountdown < 1) {
                dice[0] = random.nextInt(6) + 1;
                dice[1] = random.nextInt(6) + 1;
                diceCountdown = 20; // Tieing things to the framerate is bad, but oh well

                die1.setDrawable(diceTextures.get(dice[0]-1));
                die2.setDrawable(diceTextures.get(dice[1]-1));


            }
        } else {
            diceCountdown -= 1;
            if(diceCountdown < 1) {
                setState(State.MOVING);
            }
        }

        die1.draw(batch, 50);
        die2.draw(batch, 50);
    }

    public void renderMoves(Batch batch) {
        screen.getFont().draw(batch, "Moves left: " + moves, 100, 100);
    }

    public void renderChoice(Batch batch) {
        guess.draw(batch, 50);
        accuse.draw(batch, 50);
    }

    public void setupDice() {
        for(int i = 1; i <= 6; i++) {
            diceTextures.add(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("d"+i+".png")))));
        }

        die1 = new Image(diceTextures.get(0));
        die2 = new Image(diceTextures.get(0));

        die1.setHeight(128);
        die1.setWidth(128);
        die2.setHeight(128);
        die2.setWidth(128);

        die1.setX(736);
        die1.setY(476);

        die2.setX(1056);
        die2.setY(476);

        die1.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                hasRolled = true;
                diceCountdown = 100;
            }
        });
        die2.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                hasRolled = true;
                diceCountdown = 100;
            }
        });
    }

    public void setupGuessAccuse() {
        guess.setHeight(256);
        accuse.setHeight(256);
        guess.setWidth(192);
        accuse.setWidth(192);

        guess.setY(412);
        accuse.setY(412);

        guess.setX(736);
        accuse.setX(992);

        guess.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                isAccusing = false;
                setState(State.GUESSCHAR);
            }
        });

        accuse.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                isAccusing = true;
                setState(State.GUESSCHAR);
            }
        });
    }

    public void setupCards() {
        for(Card c : screen.getCards()) {
            cards.put(c, new Image(c.getImage()));
        }

        for(Card c : screen.getCharacters()) {
            final Image i = cards.get(c);
            int x = 608 + (screen.getCharacters().indexOf(c) % 3 * 256);
            int y = screen.getCharacters().indexOf(c) / 3F < 1 ? 300 : 600;
            i.setY(y);
            i.setX(x);
            i.setHeight(256);
            i.setWidth(192);

            i.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    for(Card card : cards.keySet()) {
                        if(cards.get(card).equals(i)) {
                            guesses.add(card);
                            break;
                        }
                    }
                    setState(State.GUESSWEAPON);
                }
            });
        }


        for(Card c : screen.getWeapons()) {
            final Image i = cards.get(c);
            int x = 608 + (screen.getWeapons().indexOf(c) % 3 * 256);
            int y = screen.getWeapons().indexOf(c) / 3F < 1 ? 300 : 600;
            i.setY(y);
            i.setX(x);
            i.setHeight(256);
            i.setWidth(192);

            i.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    for(Card card : cards.keySet()) {
                        if(cards.get(card).equals(i)) {
                            guesses.add(card);
                            break;
                        }
                    }
                    setState(State.GUESSROOM);
                }
            });
        }
    }

    public void renderGuessChar(Batch batch) {
        for(Card c : screen.getCharacters()) {
            cards.get(c).draw(batch, 50);
        }
    }

    public void renderGuessWeapon(Batch batch) {
        for(Card c : screen.getWeapons()) {
            cards.get(c).draw(batch, 50);
        }
    }
}
