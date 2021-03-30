package uk.ac.sussex.clue;

public class Door extends Tile {
    // the directions our room could be in
    enum directions {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    // The direction our room is in
    private directions dir;
    // x coordinate
    private int x;
    // y coordinate
    private int y;

    public Door(ClueGame screen, directions dir, int x, int y) {
        super(screen);
        this.dir = dir;
        this.x = x;
        this.y = y;
    }

    @Override
    public void onEnter(Player player, boolean isPulled) {
        getRoom().onEnter(player, false);
    }

    public directions getDir() {
        return dir;
    }

    public Tile getRoom() {
        switch(dir) {
            case UP:
                return screen.getBoard()[y-1][x];
            case DOWN:
                return screen.getBoard()[y+1][x];
            case LEFT:
                return screen.getBoard()[y][x-1];
            case RIGHT:
                return screen.getBoard()[y][x+1];
        }
        return null;
    }

    public Tile getOpposite() {
        switch(dir) {
            case UP:
                return screen.getBoard()[y+1][x];
            case DOWN:
                return screen.getBoard()[y-1][x];
            case LEFT:
                return screen.getBoard()[y][x+1];
            case RIGHT:
                return screen.getBoard()[y][x-1];
        }
        return null;
    }
}
