package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;
import java.util.Collections;
import static uk.ac.sussex.clue.GameState.State;

/**
 * The window that controls our game
 */
public class ClueGame extends Window {
    // The current state of the game
    private GameState gameState = new GameState(this);
    // Our list of players
    private ArrayList<Player> players =  new ArrayList();
    // ALL Cards in the game
    private ArrayList<Card> cards = new ArrayList();
    // Just the murder cards
    private ArrayList<Card> murderCards = new ArrayList<>();
    // 2d array of all our tiles
    private Tile[][] board;
    // the 2d array of just the characters, before interpretation
    private char[][] nativeBoard;
    // Arraylist of our rooms
    private ArrayList<Room> rooms = new ArrayList();
    // 2d array of where players are if they are here
    private Player[][] playerBoard;
    // current player
    private Player currentPlayer;
    // Our board's actual object
    private Image boardImage;
    // Shape renderer for drawing our objects
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    // Weapons
    private ArrayList<Card> weapons = new ArrayList<>();
    // Characters
    private ArrayList<Card> characters = new ArrayList<>();
    // rooms
    private ArrayList<Card> roomCards = new ArrayList<>();
    // Journal image
    private Image journalButton;
    // Cards image
    private Image cardsButton;
    // Our background image
    private Image backgroundImage = new Image(new Texture(Gdx.files.internal("gameBackground.png")));
    // Whether or not we've begun. Used for resetting tiles after a turn
    private boolean hasBegun = false;
    // This is an array of tiles that have already been checked by the movement algorithm
    private ArrayList<Tile> checkedTiles = new ArrayList<>();
    // This is a list of which tiles we should walk over to get to our target
    private ArrayList<Tile> path = new ArrayList<>();


    /**
     * Constructor for this class
     *
     * Creates all our instance fields, organizes the board from the files, and sets the game up
     * @param config A string in the format "ab;ab" with a being a player character, and b being either p or c, depending on whether it's a player or a computer
     */
    public ClueGame(String config) {

        // Create our background image first
        backgroundImage.setPosition(0, 0);
        backgroundImage.setSize(1920, 1080);

        // Create all of our character cards
        for(Card.Characters c : Card.Characters.values()) {
            Card card = new Card(c);
            cards.add(card);
            characters.add(card);
        }

        // Create all of our weapon cards
        for(Card.Weapons w : Card.Weapons.values()) {
            Card card = new Card(w);
            cards.add(card);
            weapons.add(card);
        }

        // Create all of our room cards
        for(Card.Rooms r : Card.Rooms.values()) {
            Card card = new Card(r);
            cards.add(card);
            roomCards.add(card);
            // Also create the rooms along with the card
            rooms.add(new Room(this, card));
        }


        // Let's split up the config then
        String[] configs = config.split(";");
        for(String s : configs) {
            // Get the first symbol to determine the character, then make them a computer if there's a C in there
            players.add(new Player(getCharacterFromSymbol(s.substring(0, 1)), s.contains("c"), this));
        }
        currentPlayer = players.get(0);
        // Give out the cards
        giveCards();

        // Now, analyze the board from the file
        String s = Gdx.files.internal("board.txt").readString();
        String[] s2 = s.split("\n");
        s2[s2.length-1] += "x"; //Character we'll ignore :)

        // create our two boards, one that's just a character 2d array, one that's a player 2d array
        nativeBoard = new char[s2.length][s2[0].length()+1];
        playerBoard = new Player[nativeBoard.length][nativeBoard[0].length];
        // Convert the string to the native array
        for(int i = 0; i < s2.length; i++) {
            nativeBoard[i] = s2[i].substring(0, s2[i].length()-1).toCharArray();
        }

        // Translate the native board into an actual board
        translateBoard();

        // Get the image for the board from the file
        boardImage = new Image(new Texture(Gdx.files.internal("board.png")));

        // Set its proportions based on the size of the board
        boardImage.setHeight(nativeBoard.length*32);
        boardImage.setWidth(nativeBoard[0].length*32);
        boardImage.setY((1080/2)-(nativeBoard.length*16));
        boardImage.setX((1920/2)-(nativeBoard[0].length*16));

        // Add the listener to our board
        boardImage.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                // The listener converts the x/y coordinates into the grid our board uses
                int boardY = (int) Math.floor(((boardImage.getImageHeight()-y)/32));
                int boardX = (int) Math.floor(x/32);
                // And if we can actually click on the tile
                if(!currentPlayer.getTile().getMoves().contains(board[boardY][boardX])) {
                    return;
                }
                // Then we do click on it
                board[boardY][boardX].clicked(currentPlayer);
            }
        });
        // Add the image as an actor
        addActor(boardImage);
        // Setup the UI
        setupUI();

        // Setup the cards
        gameState.setupCards();
        // Set our currentPlayer to the last player in the game
        currentPlayer = players.get(players.size()-1);
        // Then we begin
        hasBegun = true;

    }

    /**
     * @return our list of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the character card from any given letter in a string. See {@link NewGame#getSymbolFromCharacter(Card.Characters)}
     * @param s The string to convert
     * @return a card representing a character
     */
    public Card getCharacterFromSymbol(String s) {
        Card.Characters c = Card.Characters.SCARLET;
        switch(s) {
            case "w":
                c = Card.Characters.WHITE;
                break;
            case "g":
                c = Card.Characters.GREEN;
                break;
            case "e":
                c = Card.Characters.PEACOCK;
                break;
            case "l":
                c = Card.Characters.PLUM;
                break;
            case "s":
                c = Card.Characters.SCARLET;
                break;
            case "m":
                c = Card.Characters.MUSTARD;
                break;
            default:
                System.exit(2);
        }
        // Loop through all the cards and find one with the right character
        for(Card card : cards) {
            if(card.isType(Card.Types.CHARACTER) && card.getCard().equals(c.name().toLowerCase())) {
                return card;
            }
        }
        return null;
    }

    /**
     * Translates the board from the native board into the actual 2d tile array
     *
     * Each character is translated into a different tile
     */
    public void translateBoard() {
        board = new Tile[nativeBoard.length][nativeBoard[0].length];
        // Loop through each tile
        for(int i = 0; i < nativeBoard.length; i++) {
            for(int ii = 0; ii < nativeBoard[0].length; ii++) {
                switch(nativeBoard[i][ii]) {

                    // There's nothing here, make an empty space
                    case ' ':
                        board[i][ii] = new Space(this);
                        break;

                    // There's a tile, make a tile
                    case 't':
                        board[i][ii] = new Tile(this);
                        break;

                    // Roll-again tile
                    case 'b':
                        board[i][ii] = new Rollagain(this);
                        break;

                    // Free suggestion tile
                    case 'e':
                        board[i][ii] = new Freesuggestion(this);
                        break;

                    // Door pointing left
                    case '<':
                        board[i][ii] = new Door(this, Door.directions.LEFT, ii, i);
                        break;

                    // Door pointing up
                    case '^':
                        board[i][ii] = new Door(this, Door.directions.UP, ii, i);
                        break;

                    // Door pointing right
                    case '>':
                        board[i][ii] = new Door(this, Door.directions.RIGHT, ii, i);
                        break;

                    // Door pointing down
                    case 'v':
                        board[i][ii] = new Door(this, Door.directions.DOWN, ii, i);
                        break;

                    // If it's Miss Scarlet's starting location
                    case 'm':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("scarlet")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;

                    // If it's Colonel Mustard's starting location
                    case 'u':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("mustard")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;

                    // If it's Miss White's starting location
                    case 'w':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("white")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;

                    // If it's Mr. Green's starting location
                    case 'g':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("green")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;

                    // If it's Mrs. Peacock's starting location
                    case 'p':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("peacock")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;

                    // If it's Professor Plum's starting location
                    case 'r':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("plum")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;

                    // It's the study
                    case 's':
                        board[i][ii] = getRoomFromType(Card.Rooms.STUDY);
                        break;

                    // It's the library
                    case 'l':
                        board[i][ii] = getRoomFromType(Card.Rooms.LIBRARY);
                        break;

                    // It's the ballroom
                    case 'a':
                        board[i][ii] = getRoomFromType(Card.Rooms.BALLROOM);
                        break;

                    // It's the billiards room
                    case 'i':
                        board[i][ii] = getRoomFromType(Card.Rooms.BILLIARDS);
                        break;

                    // It's the conservatory
                    case 'c':
                        board[i][ii] = getRoomFromType(Card.Rooms.CONSERVATORY);
                        break;

                    // It's the kitchen
                    case 'k':
                        board[i][ii] = getRoomFromType(Card.Rooms.KITCHEN);
                        break;

                    // It's the dining room
                    case 'd':
                        board[i][ii] = getRoomFromType(Card.Rooms.DINING);
                        break;

                    // It's the lounge
                    case 'o':
                        board[i][ii] = getRoomFromType(Card.Rooms.LOUNGE);
                        break;

                    // It's the hall
                    case 'h':
                        board[i][ii] = getRoomFromType(Card.Rooms.HALL);
                        break;
                }
            }
        }

        // Let's sort out the passages
        for(int y = 0; y < board.length; y++) {
            for(int x = 0; x < board[0].length; x++) {
                Tile t = board[y][x];
                // If it's not a room, we need not bother
                if(!Room.class.isInstance(t)) {
                    continue;
                }

                // loop through adjacent tiles
                for(Tile t2 : getAdjacent(y, x)) {
                    // If it's the same room, continue
                    if(t.equals(t2)) {
                        continue;
                    }

                    // If it's a room, setup a passage
                    if(Room.class.isInstance(t2)) {
                        Room r1 = (Room) t;
                        Room r2 = (Room) t2;
                        r1.addPassage(r2);
                        r2.addPassage(r1);

                    // Otherwise it's a door, so let's connect to the other side
                    } else if(Door.class.isInstance(t2)) {
                        Room r = (Room) t;
                        r.addDoor((Door) t2);
                    }

                }
            }
        }

        // Move all our players to their right locations
        movePlayers();
    }

    /**
     * @return returns the board of tiles
     */
    public Tile[][] getBoard() {
        return board;
    }

    /**
     * Gets the room that has the type given
     * @param room The type of room we want
     * @return the room
     */
    public Room getRoomFromType(Card.Rooms room) {
        for(Room r : rooms) {
            if(r.getRoomtype().equals(room)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Get adjacent tiles to the given co-ordinate set
     * @param y y coordinate
     * @param x x coordinate
     * @return a list of tiles adjacent to this tile
     */
    public ArrayList<Tile> getAdjacent(int y, int x) {
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        // If we're not in the upper row, get the tile above us
        if(y > 0) {
            // If it's a door, get the room instead
            if(Door.class.isInstance(board[y-1][x])) {
                Door d = (Door) board[y-1][x];
                tiles.add(d.getRoom());
            }
            tiles.add(board[y-1][x]);
        }
        // if we're not in the lower row, get the tile below us
        if(y < board.length-1) {
            // If it's a door, get the room instead
            if(Door.class.isInstance(board[y+1][x])) {
                Door d = (Door) board[y+1][x];
                tiles.add(d.getRoom());
            }
            tiles.add(board[y+1][x]);
        }
        // if we're not in the left-most column, get the tile on our left
        if(x > 0) {
            // If it's a door, get the room instead
            if(Door.class.isInstance(board[y][x-1])) {
                Door d = (Door) board[y][x-1];
                tiles.add(d.getRoom());
            }
            tiles.add(board[y][x-1]);
        }
        // if we're not in the right-most column, get the tile on our right
        if(x < board[0].length-1) {
            // If it's a door, get the room instead
            if(Door.class.isInstance(board[y][x+1])) {
                Door d = (Door) board[y][x+1];
                tiles.add(d.getRoom());
            }
            tiles.add(board[y][x+1]);
        }
        // Then return that list of tiles
        return tiles;

    }

    /**
     * Moves the chips around the playerBoard, which is used to render where the players are on the screen
     */
    public void movePlayers() {
        for(int i = 0; i < playerBoard.length; i++) {
            playerBoard[i] = new Player[playerBoard[0].length];
        }
        // Loop through all of the players
        playerLoop: for(Player p : players) {
            // Then loop through the entire board for each player
            for(int i = 0; i < board.length; i++) {
                for(int ii = 0; ii < board[0].length; ii++) {
                    if(board[i][ii].equals(p.getTile())) {
                        // If this is the tile we're in
                        if(playerBoard[i][ii] != null) {
                            // And there's someone here, continue
                            continue;
                        } else {
                            // And there's noone here
                            if(Room.class.isInstance(board[i][ii])) {
                                // If it's a room, we calculate where in the room to show ourselves
                                boolean hasFoundEqual = false;
                                for(Tile t : getAdjacent(i, ii)) {
                                    if(t.equals(board[i][ii])) {
                                        hasFoundEqual = true;
                                    }
                                }
                                if (hasFoundEqual) {
                                    playerBoard[i][ii] = p;
                                    continue  playerLoop;
                                } else {
                                    continue;
                                }
                            } else {
                                // Otherwise just place ourselves in the tile
                                playerBoard[i][ii] = p;
                                continue playerLoop;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Renders each frame of the game. After drawing certain UI elements and the board, calls {@link GameState#render()}
     * @param delta
     */
    @Override
    public void render(float delta) {

        // Clear the screen first
        Gdx.gl.glClearColor(1, 1, 1, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setProjectionMatrix(MainController.instance.getCamera().combined);
        batch.begin();

        // Draw our background and board
        backgroundImage.draw(batch, 1);
        boardImage.draw(batch, 50);

        // Draw our UI Info, like current player and escape
        font.setColor(Color.BLACK);
        font.draw(batch, "Current player: " + getCurrentPlayer().getCharacter().getName(), 100, 150);
        font.draw(batch, "Press esc to return", 1300, 150);

        // Draw our journal and cardhand buttons
        journalButton.draw(batch, 50);
        cardsButton.draw(batch, 50);
        batch.end();

        int x0 = (int) boardImage.getX();
        int y0 = (int) boardImage.getY() + (int) boardImage.getHeight();

        // Move our players around
        movePlayers();
        // Then loop through all our players
        for(Player p : players) {

            // get the X/Y coordinates of our player
            int pX = 0;
            int pY = 0;
            loop: for(int y = 0; y < playerBoard.length; y++) {
                for(int x = 0; x < playerBoard[0].length; x++) {
                    if(playerBoard[y][x] != null && playerBoard[y][x].equals(p)) {
                        pX = x;
                        pY = y;
                        break loop;
                    }
                }
            }

            // Then colour in a circle on the tile we're in
            shapeRenderer.setColor(p.getColour());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(x0+pX*32+16, y0-pY*32-16, 16);

            // If we're the current player, fill in a small yellow circle
            if(currentPlayer.equals(p)) {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.circle(x0+pX*32+16, y0-pY*32-16, 8);
            }
            shapeRenderer.end();
        }

        // Call gameState.render()
        batch.begin();
        gameState.render();
        batch.end();

    }

    /**
     * Sets our colour and sets the gamestate to Rolling
     */
    @Override
    public void show() {
        super.show();
        font.setColor(Color.BLACK);
        gameState.setState(State.ROLLING);
    }

    /**
     * @return Gets our sprite batch
     */
    public Batch getBatch() {
        return batch;
    }

    /**
     * @return The {@link GameState} we're using
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Cycles through all the players to select the next player who hasn't made an accusation
     */
    public void nextPlayer() {

        // If we've begun, reset all the tiles that need resetting
        // We only do this if we've begun, since we'd otherwise
        // End up nullPointering as some tiles haven't been created
        if(hasBegun) {
            for(Tile[] array : board) {
                for(Tile tile : array) {
                    tile.reset();
                }
            }
        }

        int i = 0;
        // Loop through the players untill we find one who hasn't accused
        currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
        while(currentPlayer.isDidAccuse() && i < players.size()) {
            currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
            i++;
        }
    }

    /**
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * If we're not in a tile and we have no moves, we move on to the next player.
     * @see {@link Tile#onEnter(Player, boolean)}
     */
    public void isInRoom() {
        if(!Room.class.isInstance(currentPlayer.getTile()) && gameState.getMoves() < 1) {
            gameState.setState(State.ROLLING);
        }
    }

    /**
     * Shuffles the cards, picks the murder cards, hands the rest out, then shuffle them again
     */
    public void giveCards() {
        // shuffle the cards
        Collections.shuffle(weapons);
        Collections.shuffle(characters);
        Collections.shuffle(roomCards);

        // Pick our murder cards
        murderCards.add(characters.get(0));
        murderCards.add(weapons.get(0));
        murderCards.add(roomCards.get(0));

        // Hand out the characters first
        for(Card c : characters.subList(1, characters.size())) {
            currentPlayer.giveCard(c);
            nextPlayer();
        }

        // Then the weapons
        for(Card c : weapons.subList(1, weapons.size())) {
            currentPlayer.giveCard(c);
            nextPlayer();
        }

        // Then the rooms
        for(Card c : roomCards.subList(1, roomCards.size())) {
            currentPlayer.giveCard(c);
            nextPlayer();
        }

        // Then shuffle our deck again
        Collections.shuffle(weapons);
        Collections.shuffle(characters);
        Collections.shuffle(roomCards);
    }

    /**
     * @return the list of characters
     */
    public ArrayList<Card> getCharacters() {
        return characters;
    }

    /**
     * @return the list of weapons
     */
    public ArrayList<Card> getWeapons() {
        return weapons;
    }

    /**
     * @return the list of room cards
     */
    public ArrayList<Card> getRoomCards() {
        return roomCards;
    }

    /**
     * @return the full list of cards
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * @return the list of murder cards
     */
    public ArrayList<Card> getMurderCards() {
        return murderCards;
    }

    /**
     *  Move the player to the room indicated by the given card
     * @param c The room we will be moving to
     */
    public void movePlayer(Card c) {
        // Loop through all of the players
        for(Player p : players) {
            if(p.getCharacter().equals(c) && !p.equals(currentPlayer) && !p.getTile().equals(currentPlayer.getTile())) {
                p.getTile().onLeave();
                currentPlayer.getTile().onEnter(p, true);
            }
        }
    }

    /**
     *  Handles keypresses. calls {@link GameState#pressContinue()} on a spacebar press, and {@link GameState#pressEscape()} on escape
     * @param keycode the key that is pressed, @see com.badlogic.gdx.Input#Keys
     */
    @Override
    public void keyDown(int keycode) {
        if(keycode == Input.Keys.SPACE) {
            gameState.pressContinue();
        } else if(keycode == Input.Keys.ESCAPE) {
            gameState.pressEscape();
        }
    }

    /**
     * Sets up our UI elements, that being the card button and the journal button
     */
    public void setupUI() {
        // Create the button from the file
        journalButton = new Image(new Texture(Gdx.files.internal("journal.png")));

        // Set the proportions
        journalButton.setX(0);
        journalButton.setY(680);
        journalButton.setWidth(192);
        journalButton.setHeight(256);

        // Add a listener to the journal
        journalButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                // If we're in the moving state, go to the journal
                if(gameState.getState().equals(State.MOVING)) {
                    gameState.setState(State.JOURNAL);
                }
            }
        });

        // Create the button from the file
        cardsButton = new Image(new Texture(Gdx.files.internal("cards.png")));

        // Set the proportions
        cardsButton.setX(1728);
        cardsButton.setY(680);
        cardsButton.setWidth(192);
        cardsButton.setHeight(256);

        // Add a listener to the hand of cards
        cardsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                // If we're in the moving state, go to the card hand
                if(gameState.getState().equals(State.MOVING)) {
                    gameState.setState(State.CARDS);
                }
            }
        });

        // Add both as actors so they're clickable
        addActor(cardsButton);
        addActor(journalButton);


    }

    /**
     * Exits the game, and goes to the main menu
     */
    public void exitGame() {
        MainController.instance.setScreen(new Menu());
    }

    /**
     * Calculates moves by starting at the beginning, branching out as far as possible while never having 2 paths to the same place.
     *
     * Then we work our way backwards from our goal, as there's only one tile leading to each tile, so we can't loop
     */
    public void calculateMoves() {
        // Make sure we're an AI
        if(!currentPlayer.isAI()) {
            return;
        }

        // update our goal
        currentPlayer.pickGoal();

        // Clear the path
        checkedTiles.clear();
        path.clear();

        // Add each tile around the current one, as a starting point
        checkedTiles.addAll(currentPlayer.getTile().getMoves());
        boolean didWeAddAnyTiles = true;

        // Clear the paths of each tile
        for(Tile[] tiles : board) {
            for(Tile t : tiles) {
                t.resetAIMoves();
            }
        }
        while(didWeAddAnyTiles) {
            didWeAddAnyTiles = false;

            ArrayList<Tile> toBeAddedToCheckedTiles = new ArrayList<>();
            // go through each tile we need to check
            for(Tile t : checkedTiles) {
                ArrayList<Tile> moves = t.getMoves();
                // Go through each tile we can move to from there
                for(Tile t2 : moves) {
                    // If it's not in the list already
                    if(!checkedTiles.contains(t2)) {
                        // We validate the move
                        t.addAIMove(t2);
                        // and add it to the list
                        toBeAddedToCheckedTiles.add(t2);
                        didWeAddAnyTiles = true;
                    }
                }
            }
            // Then add the list to our list
            checkedTiles.addAll(toBeAddedToCheckedTiles);

        }

        path.add(currentPlayer.getGoal());

        // Start at our goal, then work our way back
        // Each tile only has one tile leading it, so by working our way back
        // We're sure that we'll get the most effective path
        Tile currentSearch = currentPlayer.getGoal();
        while(!currentPlayer.getTile().getMoves().contains(currentSearch)) {
            for(Tile[] tiles : board) {
                for(Tile t : tiles) {
                    if(t.getAiMoves().contains(currentSearch)) {
                        path.add(t);
                        currentSearch = t;
                    }
                }
            }
        }


    }

    /**
     * @return returns a list of rooms
     */
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    /**
     *  Moves the AI to the next step in its path
     */
    public void moveAI() {
        if(currentPlayer.isAI()) {
            Tile t = path.get(path.size()-1);
            path.remove(t);
            t.clicked(currentPlayer);
        }
    }
}
