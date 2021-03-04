package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

public class ClueGame extends Window {
    // Our list of players
    private ArrayList<Player> players;
    // ALL Cards in the game
    private ArrayList<Card> cards;
    // 2d array of all our tiles
    private Tile[][] board;
    // the 2d array of just the characters, before interpretation
    private char[][] nativeBoard;

    public ClueGame(String config) {

        players = new ArrayList<Player>();
        cards = new ArrayList<Card>();

        for(Card.Characters c : Card.Characters.values()) {
            cards.add(new Card(c));
        }

        for(Card.Weapons w : Card.Weapons.values()) {
            cards.add(new Card(w));
        }

        for(Card.Rooms r : Card.Rooms.values()) {
            cards.add(new Card(r));
        }


        String[] configs = config.split(";");
        for(String s : configs) {
            // Get the first symbol to determine the character, then make them a computer if there's a C in there
            players.add(new Player(getCharacterFromSymbol(s.substring(0, 1)), s.contains("c")));
        }

        String s = Gdx.files.internal("board.txt").readString();
        String[] s2 = s.split("\n");

        nativeBoard = new char[s2.length][s2[0].length()-1];
        for(int i = 0; i < s2.length; i++) {
            nativeBoard[i] = s2[i].substring(0, s2[i].length()-1).toCharArray();
        }

        translateBoard();

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
                        break;
                    case 't':
                        board[i][ii] = new Tile(this);
                        break;
                    case '<':
                        board[i][ii] = new Door(this, Door.directions.LEFT);
                        break;
                    case '^':
                        board[i][ii] = new Door(this, Door.directions.UP);
                        break;
                    case '>':
                        board[i][ii] = new Door(this, Door.directions.RIGHT);
                        break;
                    case 'v':
                        board[i][ii] = new Door(this, Door.directions.DOWN);
                        break;
                    case 'm':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard() == "scarlet") {
                                board[i][ii].onEnter(p);
                            }
                        }
                    case 'u':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard() == "mustard") {
                                board[i][ii].onEnter(p);
                            }
                        }
                    case 'w':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard() == "white") {
                                board[i][ii].onEnter(p);
                            }
                        }
                    case 'g':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard() == "green") {
                                board[i][ii].onEnter(p);
                            }
                        }
                    case 'p':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard() == "peacock") {
                                board[i][ii].onEnter(p);
                            }
                        }
                    case 'r':
                        board[i][ii] = new Tile(this);
                        for(Player p : players) {
                            if(p.getCharacter().getCard() == "plum") {
                                board[i][ii].onEnter(p);
                            }
                        }



                }
            }
        }
    }
}
