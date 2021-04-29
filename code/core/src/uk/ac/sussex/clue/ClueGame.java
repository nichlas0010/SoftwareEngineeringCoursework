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
import java.util.LinkedList;

import static uk.ac.sussex.clue.GameState.State;

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


    public ClueGame(String config) {

        backgroundImage.setPosition(0, 0);
        backgroundImage.setSize(1920, 1080);

        for(Card.Characters c : Card.Characters.values()) {
            Card card = new Card(c);
            cards.add(card);
            characters.add(card);
        }

        for(Card.Weapons w : Card.Weapons.values()) {
            Card card = new Card(w);
            cards.add(card);
            weapons.add(card);
        }

        for(Card.Rooms r : Card.Rooms.values()) {
            Card card = new Card(r);
            cards.add(card);
            roomCards.add(card);
            rooms.add(new Room(this, card));
        }


        String[] configs = config.split(";");
        for(String s : configs) {
            // Get the first symbol to determine the character, then make them a computer if there's a C in there
            players.add(new Player(getCharacterFromSymbol(s.substring(0, 1)), s.contains("c"), this));
        }
        currentPlayer = players.get(0);
        giveCards();

        String s = Gdx.files.internal("board.txt").readString();
        String[] s2 = s.split("\n");
        s2[s2.length-1] += "x"; //Character we'll ignore :)

        nativeBoard = new char[s2.length][s2[0].length()+1];
        playerBoard = new Player[nativeBoard.length][nativeBoard[0].length];
        for(int i = 0; i < s2.length; i++) {
            nativeBoard[i] = s2[i].substring(0, s2[i].length()-1).toCharArray();
        }

        translateBoard();

        boardImage = new Image(new Texture(Gdx.files.internal("board.png")));
        boardImage.setHeight(nativeBoard.length*32);
        boardImage.setWidth(nativeBoard[0].length*32);
        boardImage.setY((1080/2)-(nativeBoard.length*16));
        boardImage.setX((1920/2)-(nativeBoard[0].length*16));

        boardImage.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                int boardY = (int) Math.floor(((boardImage.getImageHeight()-y)/32));
                int boardX = (int) Math.floor(x/32);
                if(!currentPlayer.getTile().getMoves().contains(board[boardY][boardX])) {
                    return;
                }
                board[boardY][boardX].clicked(currentPlayer);
            }
        });
        addActor(boardImage);
        setupUI();

        gameState.setupCards();
        currentPlayer = players.get(players.size()-1);
        hasBegun = true;

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

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
        for(Card card : cards) {
            if(card.isType(Card.Types.CHARACTER) && card.getCard().equals(c.name().toLowerCase())) {
                return card;
            }
        }
        return null;
    }

    public void translateBoard() {
        board = new Tile[nativeBoard.length][nativeBoard[0].length];
        for(int i = 0; i < nativeBoard.length; i++) {
            for(int ii = 0; ii < nativeBoard[0].length; ii++) {
                switch(nativeBoard[i][ii]) {
                    case ' ':
                        board[i][ii] = new Space(this);
                        break;
                    case 't':
                        board[i][ii] = new Tile(this);
                        break;
                    case 'b':
                        board[i][ii] = new Rollagain(this);
                        break;
                    case 'e':
                        board[i][ii] = new Freesuggestion(this);
                        break;
                    case '<':
                        board[i][ii] = new Door(this, Door.directions.LEFT, ii, i);
                        break;
                    case '^':
                        board[i][ii] = new Door(this, Door.directions.UP, ii, i);
                        break;
                    case '>':
                        board[i][ii] = new Door(this, Door.directions.RIGHT, ii, i);
                        break;
                    case 'v':
                        board[i][ii] = new Door(this, Door.directions.DOWN, ii, i);
                        break;
                    case 'm':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("scarlet")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;
                    case 'u':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("mustard")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;
                    case 'w':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("white")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;
                    case 'g':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("green")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }

                        break;
                    case 'p':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("peacock")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;
                    case 'r':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard().equals("plum")) {
                                board[i][ii].onEnter(p, true);
                            }
                        }
                        break;
                    case 's':
                        board[i][ii] = getRoomFromType(Card.Rooms.STUDY);
                        break;
                    case 'l':
                        board[i][ii] = getRoomFromType(Card.Rooms.LIBRARY);
                        break;
                    case 'a':
                        board[i][ii] = getRoomFromType(Card.Rooms.BALLROOM);
                        break;
                    case 'i':
                        board[i][ii] = getRoomFromType(Card.Rooms.BILLIARDS);
                        break;
                    case 'c':
                        board[i][ii] = getRoomFromType(Card.Rooms.CONSERVATORY);
                        break;
                    case 'k':
                        board[i][ii] = getRoomFromType(Card.Rooms.KITCHEN);
                        break;
                    case 'd':
                        board[i][ii] = getRoomFromType(Card.Rooms.DINING);
                        break;
                    case 'o':
                        board[i][ii] = getRoomFromType(Card.Rooms.LOUNGE);
                        break;
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
                if(!Room.class.isInstance(t)) {
                    continue;
                }
                for(Tile t2 : getAdjacent(y, x)) {
                    if(t.equals(t2)) {
                        continue;
                    }
                    if(Room.class.isInstance(t2)) {
                        Room r1 = (Room) t;
                        Room r2 = (Room) t2;
                        r1.addPassage(r2);
                        r2.addPassage(r1);
                    } else if(Door.class.isInstance(t2)) {
                        Room r = (Room) t;
                        r.addDoor((Door) t2);
                    }

                }
            }
        }

        movePlayers();
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Room getRoomFromType(Card.Rooms room) {
        for(Room r : rooms) {
            if(r.getRoomtype().equals(room)) {
                return r;
            }
        }
        return null;
    }

    public ArrayList<Tile> getAdjacent(int y, int x) {
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        if(y > 0) {
            if(Door.class.isInstance(board[y-1][x])) {
                Door d = (Door) board[y-1][x];
                tiles.add(d.getRoom());
            }
            tiles.add(board[y-1][x]);
        }
        // Below
        if(y < board.length-1) {
            if(Door.class.isInstance(board[y+1][x])) {
                Door d = (Door) board[y+1][x];
                tiles.add(d.getRoom());
            }
            tiles.add(board[y+1][x]);
        }
        // Left
        if(x > 0) {
            if(Door.class.isInstance(board[y][x-1])) {
                Door d = (Door) board[y][x-1];
                tiles.add(d.getRoom());
            }
            tiles.add(board[y][x-1]);
        }
        // Right
        if(x < board[0].length-1) {
            if(Door.class.isInstance(board[y][x+1])) {
                Door d = (Door) board[y][x+1];
                tiles.add(d.getRoom());
            }
            tiles.add(board[y][x+1]);
        }
        return tiles;

    }

    public void movePlayers() {
        for(int i = 0; i < playerBoard.length; i++) {
            playerBoard[i] = new Player[playerBoard[0].length];
        }
        playerLoop: for(Player p : players) {
            for(int i = 0; i < board.length; i++) {
                for(int ii = 0; ii < board[0].length; ii++) {
                    if(board[i][ii].equals(p.getTile())) {
                        if(playerBoard[i][ii] != null) {
                            continue;
                        } else {
                            if(Room.class.isInstance(board[i][ii])) {
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
                                playerBoard[i][ii] = p;
                                continue playerLoop;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setProjectionMatrix(MainController.instance.getCamera().combined);
        batch.begin();
        backgroundImage.draw(batch, 1);
        boardImage.draw(batch, 50);
        font.setColor(Color.BLACK);
        font.draw(batch, "Current player: " + getCurrentPlayer().getCharacter().getName(), 100, 150);
        font.draw(batch, "Press esc to return", 1300, 150);
        journalButton.draw(batch, 50);
        cardsButton.draw(batch, 50);
        batch.end();

        int x0 = (int) boardImage.getX();
        int y0 = (int) boardImage.getY() + (int) boardImage.getHeight();

        movePlayers();
        for(Player p : players) {
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
            shapeRenderer.setColor(p.getColour());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(x0+pX*32+16, y0-pY*32-16, 16);
            if(currentPlayer.equals(p)) {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.circle(x0+pX*32+16, y0-pY*32-16, 8);
            }
            shapeRenderer.end();
        }
        batch.begin();
        gameState.render();
        batch.end();

    }

    @Override
    public void show() {
        super.show();
        font.setColor(Color.BLACK);
        gameState.setState(State.ROLLING);
    }

    public Batch getBatch() {
        return batch;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void nextPlayer() {

        if(hasBegun) {
            for(Tile[] array : board) {
                for(Tile tile : array) {
                    tile.reset();
                }
            }
        }

        int i = 0;
        currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
        while(currentPlayer.isDidAccuse() && i < players.size()) {
            currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
            i++;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void isInRoom() {
        if(!Room.class.isInstance(currentPlayer.getTile()) && gameState.getMoves() < 1) {
            gameState.setState(State.ROLLING);
        }
    }

    public void giveCards() {
        Collections.shuffle(weapons);
        Collections.shuffle(characters);
        Collections.shuffle(roomCards);

        murderCards.add(characters.get(0));
        murderCards.add(weapons.get(0));
        murderCards.add(roomCards.get(0));

        for(Card c : characters.subList(1, characters.size())) {
            currentPlayer.giveCard(c);
            nextPlayer();
        }
        for(Card c : weapons.subList(1, weapons.size())) {
            currentPlayer.giveCard(c);
            nextPlayer();
        }
        for(Card c : roomCards.subList(1, roomCards.size())) {
            currentPlayer.giveCard(c);
            nextPlayer();
        }

        Collections.shuffle(weapons);
        Collections.shuffle(characters);
        Collections.shuffle(roomCards);
    }

    public ArrayList<Card> getCharacters() {
        return characters;
    }

    public ArrayList<Card> getWeapons() {
        return weapons;
    }

    public ArrayList<Card> getRoomCards() {
        return roomCards;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<Card> getMurderCards() {
        return murderCards;
    }

    public void movePlayer(Card c) {
        for(Player p : players) {
            if(p.getCharacter().equals(c) && !p.equals(currentPlayer) && !p.getTile().equals(currentPlayer.getTile())) {
                p.getTile().onLeave();
                currentPlayer.getTile().onEnter(p, true);
            }
        }
    }

    @Override
    public void keyDown(int keycode) {
        if(keycode == Input.Keys.SPACE) {
            gameState.pressContinue();
        } else if(keycode == Input.Keys.ESCAPE) {
            gameState.pressEscape();
        }
    }

    public void setupUI() {
        journalButton = new Image(new Texture(Gdx.files.internal("journal.png")));
        journalButton.setX(0);
        journalButton.setY(680);
        journalButton.setWidth(192);
        journalButton.setHeight(256);
        journalButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(gameState.getState().equals(State.MOVING)) {
                    gameState.setState(State.JOURNAL);
                }
            }
        });

        cardsButton = new Image(new Texture(Gdx.files.internal("cards.png")));
        cardsButton.setX(1728);
        cardsButton.setY(680);
        cardsButton.setWidth(192);
        cardsButton.setHeight(256);
        cardsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(gameState.getState().equals(State.MOVING)) {
                    gameState.setState(State.CARDS);
                }
            }
        });

        // Create our textbutton style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        addActor(cardsButton);
        addActor(journalButton);


    }

    public void exitGame() {
        MainController.instance.setScreen(new Menu());
    }

    public void calculateMoves() {
        if(!currentPlayer.isAI()) {
            return;
        }

        currentPlayer.pickGoal();

        checkedTiles.clear();
        path.clear();

        checkedTiles.addAll(currentPlayer.getTile().getMoves());
        boolean didWeAddAnyTiles = true;
        for(Tile[] tiles : board) {
            for(Tile t : tiles) {
                t.resetAIMoves();
            }
        }
        while(didWeAddAnyTiles) {
            didWeAddAnyTiles = false;

            ArrayList<Tile> toBeAddedToCheckedTiles = new ArrayList<>();
            for(Tile t : checkedTiles) {
                ArrayList<Tile> moves = t.getMoves();
                for(Tile t2 : moves) {
                    if(!checkedTiles.contains(t2)) {
                        t.addAIMove(t2);
                        toBeAddedToCheckedTiles.add(t2);
                        didWeAddAnyTiles = true;
                    }
                }
            }
            checkedTiles.addAll(toBeAddedToCheckedTiles);

        }

        path.add(currentPlayer.getGoal());

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

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void moveAI() {
        if(currentPlayer.isAI()) {
            Tile t = path.get(path.size()-1);
            path.remove(t);
            t.clicked(currentPlayer);
        }
    }
}
