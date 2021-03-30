package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;

public class ClueGame extends Window {
    // Our list of players
    private ArrayList<Player> players =  new ArrayList();
    // ALL Cards in the game
    private ArrayList<Card> cards = new ArrayList();
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
    // whether the player is allowed to move right now
    boolean canMove = true;
    // Our board's actual object
    private Image boardImage;
    // Shape renderer for drawing our objects
    private ShapeRenderer shapeRenderer = new ShapeRenderer();


    public ClueGame(String config) {

        for(Card.Characters c : Card.Characters.values()) {
            cards.add(new Card(c));
        }

        for(Card.Weapons w : Card.Weapons.values()) {
            cards.add(new Card(w));
        }

        for(Card.Rooms r : Card.Rooms.values()) {
            Card c = new Card(r);
            cards.add(c);
            rooms.add(new Room(this, c));
        }


        String[] configs = config.split(";");
        for(String s : configs) {
            // Get the first symbol to determine the character, then make them a computer if there's a C in there
            players.add(new Player(getCharacterFromSymbol(s.substring(0, 1)), s.contains("c")));
        }

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

        currentPlayer = players.get(0);

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

    public char[][] getNativeBoard() {
        return nativeBoard;
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
            tiles.add(board[y-1][x]);
        }
        // Below
        if(y < board.length-1) {
            tiles.add(board[y+1][x]);
        }
        // Left
        if(x > 0) {
            tiles.add(board[y][x-1]);
        }
        // Right
        if(x < board[0].length-1) {
            tiles.add(board[y][x+1]);
        }
        return tiles;

    }

    public Player getPlayerFromCard(Card c) {
        for(Player p : players) {
            if(p.getCharacter().equals(c)) {
                return p;
            }
        }
        return null;
    }

    public void movePlayers() {
        for(int i = 0; i < playerBoard.length; i++) {
            playerBoard[i] = new Player[playerBoard[0].length];
        }
        for(Player p : players) {
            for(int i = 0; i < board.length; i++) {
                for(int ii = 0; ii < board[0].length; ii++) {
                    if(board[i][ii].equals(p.getTile())) {
                        if(playerBoard[i][ii] != null && board[i][ii].equals(playerBoard[i][ii].getTile())) {
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
                                } else {
                                    continue;
                                }
                            } else {
                                playerBoard[i][ii] = p;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(MainController.instance.getCamera().combined);
        batch.begin();
        boardImage.draw(batch, 50);
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
            shapeRenderer.end();
        }
    }

    @Override
    public void show() {
        super.show();

        // Create our textbutton style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.font.setColor(Color.WHITE);

        boardImage.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                int boardY = (int) Math.floor(((boardImage.getImageHeight()-y)/32));
                int boardX = (int) Math.floor(x/32);
                board[boardY][boardX].clicked(currentPlayer);
            }
        });
        addActor(boardImage);
    }

}
