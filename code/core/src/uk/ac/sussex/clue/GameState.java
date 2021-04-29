package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;

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
    private int Countdown;
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
        ROLLING, MOVING, CHOICE, GUESSCHAR, GUESSWEAPON, GUESSROOM, SHOWGUESS, DONEGUESSING, GIVECARD, SEECARD,
        SEEMURDER, JOURNAL, CARDS
    }
    // which state we're actually in
    private State state;
    // RNG
    private Random random = new Random();
    // Our cards
    private HashMap<Card, Image> cards = new HashMap<>();
    // The guessed cards
    private ArrayList<Card> guesses = new ArrayList<>();
    // who we're waiting for
    private boolean isWaiting = false;
    // whether we're waiting for someone to click
    private Player actingPlayer;
    // our shape renderer
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    // list of images we use for the guesses
    private ArrayList<Image> guessImages = new ArrayList<>();
    // List of cards that we can hand over
    private HashMap<Card, Image> returncards = new HashMap<>();
    // returned card
    private Image returnImage;
    // Whether our fella guessed correctly
    private boolean guessCorrectly = true;
    // Whether we're rerolling since we landed on a proper tile
    private boolean reRolling = false;
    // whether we're extra-guessing
    private boolean reGuessing = false;
    // Our card that they returned along with the returned image
    private Card returnCard;



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
            case GUESSROOM:
                renderGuessRoom(batch);
                break;
            case SHOWGUESS:
                renderGuess(batch);
                break;
            case GIVECARD:
                renderGiveCard(batch);
                break;
            case SEECARD:
                renderSeeCard(batch);
                break;
            case SEEMURDER:
                renderShowMurder(batch);
                break;
            case CARDS:
                renderCardHand(batch);
                break;
            case JOURNAL:
                renderJournal(batch);
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
        // I hate switches in java.
        // So you see, because switches continue past case statements, I need to define this variable outside the switch
        // Cause it's used in both murder and show guess
        // Which means they're technically in the same scope
        // And so it complains at it being defined twice.
        // dumb stuff
        int index;
        switch(state) {
            case MOVING:
                screen.calculateMoves();
                break;
            case ROLLING:
                Countdown = 20;
                reGuessing = false;
                if(!reRolling) {
                    screen.nextPlayer();
                }
                reRolling = false;
                hasRolled = false;
                screen.addActor(die1);
                screen.addActor(die2);
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
                    } else {
                        cards.get(c).remove();
                    }
                }
                break;
            case GUESSROOM:
                for(Card c : cards.keySet()) {
                    if(c.isType(Card.Types.ROOM)) {
                        screen.addActor(cards.get(c));
                    } else {
                        cards.get(c).remove();
                    }
                }
                // If they're in a room, we just use that room as our guess
                if(Room.class.isInstance(screen.getCurrentPlayer().getTile())) {
                    Room r = (Room) screen.getCurrentPlayer().getTile();
                    guesses.add(r.getCard());
                    setState(State.SHOWGUESS);
                // otherwise we need to actually let them guess *urgh*
                }
                break;
            case SHOWGUESS:
                for(Card c : cards.keySet()) {
                    cards.get(c).remove();
                }
                Countdown = 200;
                guessImages.clear();
                for(Card c : guesses) {
                    Image i = new Image(c.getImage());
                    i.setY(540);
                    i.setX(guesses.indexOf(c)*256+608);
                    i.setWidth(192);
                    i.setHeight(256);
                    guessImages.add(i);
                }
                break;
            case DONEGUESSING:
                screen.movePlayer(guesses.get(0));
                if(isAccusing) {
                    setState(State.SEEMURDER);
                    break;
                } else {
                    playerLoop: for(int i = 0; i < screen.getPlayers().size(); i++) {
                        Player p = screen.getPlayers().get((screen.getPlayers().indexOf(screen.getCurrentPlayer())+i) % screen.getPlayers().size());
                        if(p.equals(screen.getCurrentPlayer())) {
                            continue playerLoop;
                        }
                        isWaiting = false;
                        for(Card c : guesses) {
                            if(p.getCards().contains(c)) {
                                isWaiting = true;
                                actingPlayer = p;
                                break playerLoop;
                            }
                        }
                    }

                    if(isWaiting) {
                        setState(State.GIVECARD);
                    } else {
                        if(screen.getCurrentPlayer().isAI()) {
                            screen.getCurrentPlayer().weKnowTheCards(guesses);
                        }
                        if(reGuessing && moves > 0) {
                            setState(State.MOVING);
                        } else {
                            setState(State.ROLLING);
                        }
                    }

                }
                break;
            case SEEMURDER:
                Countdown = 200;
                screen.getCurrentPlayer().setDidAccuse(true);
                guessCorrectly = true;
                for(int i = 0; i < 3; i++) {
                    if(!guesses.get(i).equals(screen.getMurderCards().get(i))) {
                        guessCorrectly = false;
                    }
                }
                guessImages.clear(); // We'll just reuse it
                index = 0;
                for(Card c : screen.getMurderCards()) {
                    Image i = new Image(c.getImage());
                    guessImages.add(i);
                    i.setHeight(256);
                    i.setWidth(192);
                    i.setY(400);
                    i.setX(540 + 256*index);
                    index++;
                }
                isWaiting = true;
                break;
            case GIVECARD:
                index = 0;
                returncards.clear();
                for(final Card c : guesses) {
                    if(actingPlayer.getCards().contains(c)) {
                        Image i = new Image(c.getImage());
                        returncards.put(c, i);
                        i.setHeight(256);
                        i.setWidth(192);
                        i.setY(400);
                        i.setX(540 + 256*index);
                        index++;
                        i.addListener(new ClickListener() {
                            public void clicked(InputEvent event, float x, float y) {
                                returnImage = new Image(c.getImage());
                                returnImage.setWidth(192);
                                returnImage.setHeight(256);
                                returnImage.setX(796);
                                returnImage.setY(400);
                                returnCard = c;
                                setState(State.SEECARD);
                            }
                        });

                        screen.addActor(i);
                    }
                }
                break;
            case SEECARD:
                for(Card c : returncards.keySet()) {
                    returncards.get(c).remove();
                }
                isWaiting = true;
                Countdown = 200;
                break;
            case CARDS:
                returncards.clear();
                int x = 0;
                for(Card c : screen.getCurrentPlayer().getCards()) {
                    Image i = new Image(c.getImage());
                    i.setHeight(256);
                    i.setWidth(192);
                    returncards.put(c, i);

                    int y = Math.floorDiv(x, 3);
                    i.setY(100+300*y);
                    i.setX(540+(x % 3)*256);
                    x++;
                }
                break;
            case JOURNAL:
                screen.addActor(screen.getCurrentPlayer().getNotes());
                break;
        }
    }

    public void renderDice(Batch batch) {
        drawBackground(batch);
        if(!hasRolled) {
            Countdown -= 1;
            if(Countdown < 1) {
                dice[0] = random.nextInt(6) + 1;
                dice[1] = random.nextInt(6) + 1;
                Countdown = 20; // Tieing things to the framerate is bad, but oh well

                die1.setDrawable(diceTextures.get(dice[0]-1));
                die2.setDrawable(diceTextures.get(dice[1]-1));

                if(screen.getCurrentPlayer().isAI()) {
                    Countdown = 100;
                    hasRolled = true;
                }


            }
        } else {
            Countdown -= 1;
            if(Countdown < 1) {
                die1.remove();
                die2.remove();
                moves = dice[0]+dice[1];
                setState(State.MOVING);
            }
        }

        die1.draw(batch, 50);
        die2.draw(batch, 50);
    }

    public void renderMoves(Batch batch) {
        screen.getFont().draw(batch, "Moves left: " + moves, 100, 100);
        Countdown--;
        if(Countdown < 0) {
            Countdown = 10;
            screen.moveAI();
        }
    }

    public void renderChoice(Batch batch) {
        drawBackground(batch);
        guess.draw(batch, 50);
        accuse.draw(batch, 50);

        if(screen.getCurrentPlayer().isAI()) {
            Image i;
            if(screen.getCurrentPlayer().doWeAccuse()) {
                i = accuse;
            } else {
                i = guess;
            }

            ClickListener listener = (ClickListener) i.getListeners().get(0);
            listener.clicked(new InputEvent(), 0, 0);
        }
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
                Countdown = 100;
            }
        });
        die2.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                hasRolled = true;
                Countdown = 100;
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

        int x = 0;
        for(Card c : screen.getRoomCards()) {
            final Image i = cards.get(c);
            int y = Math.floorDiv(x, 3);
            i.setY(100+300*y);
            i.setX(600+(x % 3)*256);
            x++;
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
                    setState(State.SHOWGUESS);
                }
            });
        }


    }

    public void renderGuessChar(Batch batch) {
        drawBackground(batch);
        for(Card c : screen.getCharacters()) {
            cards.get(c).draw(batch, 50);
        }

        // let the AI pick a card
        if(screen.getCurrentPlayer().isAI()) {
            Card c = screen.getCurrentPlayer().pickCard(Card.Types.CHARACTER);
            clickCard(c);
        }
    }

    public void renderGuessWeapon(Batch batch) {
        drawBackground(batch);
        for(Card c : screen.getWeapons()) {
            cards.get(c).draw(batch, 50);
        }

        // let the AI pick a card
        if(screen.getCurrentPlayer().isAI()) {
            Card c = screen.getCurrentPlayer().pickCard(Card.Types.WEAPON);
            clickCard(c);
        }
    }

    public void renderGuessRoom(Batch batch) {
        drawBackground(batch);
        for(Card c : screen.getRoomCards()) {
            cards.get(c).draw(batch, 50);
        }

        // let the AI pick a card
        if(screen.getCurrentPlayer().isAI()) {
            Card c = screen.getCurrentPlayer().pickCard(Card.Types.ROOM);
            clickCard(c);
        }
    }

    public void renderGuess(Batch batch) {
        Countdown--;
        drawBackground(batch);
        BitmapFont f = screen.getFont();
        f.setColor(Color.WHITE);
        f.draw(batch, screen.getCurrentPlayer().getCharacter().getName()+" Has chosen:", 608, 900);
        for(Image i : guessImages) {
            i.draw(batch, 50);
        }
        if(Countdown < 0) {
            setState(State.DONEGUESSING);
        }
    }

    public void pressContinue() {
        isWaiting = false;
    }

    public void renderGiveCard(Batch batch) {
        drawBackground(batch);
        screen.getFont().setColor(Color.WHITE);
        if(isWaiting) {
            screen.getFont().draw(batch, "Current player is: "+actingPlayer.getCharacter().getName()+"\nPress space to continue", 750, 540);
        } else {
            screen.getFont().draw(batch, "Choose a card to show to "+screen.getCurrentPlayer().getCharacter().getName(), 750, 900);
            for(Card c : returncards.keySet()) {
                returncards.get(c).draw(batch, 50);
            }
        }

        if(actingPlayer.isAI()) {
            int randomlyPicked = random.nextInt(returncards.size());
            int soFar = 0;
            for(Card c : returncards.keySet()) {
                if(soFar != randomlyPicked) {
                    soFar++;
                    continue;
                }
                ClickListener listener = (ClickListener) returncards.get(c).getListeners().get(0);
                listener.clicked(new InputEvent(), 0, 0);
                break;
            }
        }
    }

    public void drawBackground(Batch batch) {
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
        shapeRenderer.rect(0, 0, 1920, 1080);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    public void renderSeeCard(Batch batch) {
        drawBackground(batch);
        screen.getFont().setColor(Color.WHITE);
        if(screen.getCurrentPlayer().isAI()) {
            for(Card c : returncards.keySet()) {
                if(c.equals(returnCard)) {
                    screen.getCurrentPlayer().knowCard(c);
                }
            }
        }
        if(Countdown < 0 || screen.getCurrentPlayer().isAI()) {
            if(moves < 1 || !reGuessing) {
                setState(State.ROLLING);
            } else {
                setState(State.MOVING);
            }
            return;
        }
        if(isWaiting) {
            screen.getFont().draw(batch, "Current player is: "+screen.getCurrentPlayer().getCharacter().getName()+"\nPress space to continue", 750, 540);
        } else {
            screen.getFont().draw(batch, actingPlayer.getCharacter().getName()+ " showed you:", 750, 900);
            returnImage.draw(batch, 50);
            Countdown--;
        }
    }

    public void renderShowMurder(Batch batch) {
        drawBackground(batch);
        BitmapFont f = screen.getFont();
        f.setColor(Color.WHITE);
        if(screen.getCurrentPlayer().isAI() && !guessCorrectly) {
            isWaiting = false;
        }
        if(isWaiting) {
            if(guessCorrectly) {
                f.draw(batch, "Congratulations, you won! The murder cards were:", 608, 900);
                f.draw(batch, "Press space to exit", 608, 300);
            } else {
                f.draw(batch, "Commiserations, you lost! The murder cards were:", 608, 900);
                f.draw(batch, "Press space to continue", 608, 300);
            }
            for(Image i : guessImages) {
                i.draw(batch, 50);
            }
        } else {
            if(guessCorrectly) {
                MainController.instance.setScreen(new Menu());
            } else {
                boolean isAnyoneThere = false;
                for(Player p : screen.getPlayers()) {
                    if(!p.isDidAccuse()) {
                        isAnyoneThere = true;
                    }
                }
                if(isAnyoneThere) {
                    setState(State.ROLLING);
                } else {
                    f.draw(batch, "Commiserations, everyone lost!", 608, 900);
                    Countdown--;
                    if(Countdown < 1) {
                        MainController.instance.setScreen(new Menu());
                    }
                }

            }
        }
    }

    public State getState() {
        return state;
    }

    public void pressEscape() {
        if(state.equals(State.JOURNAL)) {
            screen.getCurrentPlayer().getNotes().remove();
            setState(State.MOVING);
        } else if(state.equals(State.CARDS)) {
           setState(State.MOVING);
        } else if(state.equals(State.MOVING)) {
            screen.exitGame();
        }
    }

    public void reRoll() {
        reRolling = true;
        setState(State.ROLLING);
    }

    public void reGuess() {
        reGuessing = true;
        setState(State.GUESSCHAR);
    }

    public void renderCardHand(Batch batch) {
        drawBackground(batch);
        for(Card c : returncards.keySet()) {
            returncards.get(c).draw(batch, 50);
        }
    }

    public void renderJournal(Batch batch) {
        drawBackground(batch);
        TextArea notes = screen.getCurrentPlayer().getNotes();
        notes.draw(batch, 50);
    }

    public HashMap<Card, Image> getCards() {
        return cards;
    }

    public void clickCard(Card c) {
        ClickListener listener = (ClickListener) cards.get(c).getListeners().get(0);
        listener.clicked(new InputEvent(), 0, 0);
    }
}
