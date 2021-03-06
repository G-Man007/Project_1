/* Game_Driver.java -- Sets mines, tile values, and checks if the game is over
 *
 * gameOver() -- Called if the player has lost by clicking on a mine, ends the game
 * gameWin() -- Called if the player has won the game and checks all the tiles before prompting user for replay
 * isEndPossible() -- Called when a flag is set to check if the mines are all flagged and tiles are all revealed
 * openTile() -- Recursive function that expands from an empty tile orthogonally
 * initBoard() -- Places mines and sets the value of surrounding mines in each tile
 *
 * placeMines() -- called by initBoard() to place all of the mines
 * setMine() -- called by placeMines() to check if a tile is a mine, if not place the mine, if yes call setMine() again
 * setRiskNum() called by initBoard() to set the value of all surrounding mines to every tile
 * */

//Swing imports
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.JButton;
//AWT imports
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//Non-GUI related imports
import java.util.Random;

class Game_Driver {
    /* Objects for Game_Driver */
    /** JFrame mGame - set equal to JFrame board in constructor from {@link #Board}, game window that user interacts with **/
    private static JFrame mGame;
    /** Tile mTileArray - 2D Tile array of used for game logic, equal coordinates to nXm game board **/
    private static Tile mTileArray[][];
    /** Random random - creates a random value, used for x,y coordinates **/
    private Random random = new Random();

    /* Member Variables for Game_Driver */
    /** mNumRows - holds the number of rows **/
    private static int mNumRows;
    /** mNumRows - holds the number of columns **/
    private static int mNumCols;
    /** mNumRows - holds the number of mines **/
    private static int mNumMines;

    /////////////////////////////////////////////////////////
    //Constructor
    /////////////////////////////////////////////////////////
    /*
     * This constructor is called in {@link #Board} to initialize the board with the already
     * checked input from the user.
     *
     * @ms.Pre-condition passes in ready to go frame with buttons already associated to tileArray
     * @ms.Post-condition Game is fully initialized
     *
     * @see #initBoard
     *
     * @param game -JFrame of the nXm board
     * @param tileArray - {@link #Tile} object array already associated with buttons
     * @param numRows - value of amount of rows in the board
     * @param numCols - value of amount of columns in the board
     * @param minCount - amount of mines user wants to place in their board
     *
    * */
    Game_Driver(JFrame game, Tile[][] tileArray, int numRows, int numCols, int mineCount) {
        mGame = game;
        mTileArray = tileArray;
        mNumRows = numRows;
        mNumCols = numCols;
        mNumMines = mineCount;
        initBoard();
    }

    /////////////////////////////////////////////////////////
    //End of game methods
    /////////////////////////////////////////////////////////

    /* gameOver()
     * @ms.Pre-condition User has lost, mine has been hit
     * @ms.Post-condition Game Board is disabled and lose frame pops up
     *
     * @see #gameWin()
     *
     * Displays all of the bombs and disables all of the buttons,
     * presents the user with a replay option.
     * */
    static void gameOver() {
        for (int i = 0; i < mNumRows; i++) {
            for (int j = 0; j < mNumCols; j++) {
                if (mTileArray[i][j].getIsMine()) {
                    mTileArray[i][j].setMineIcon();
                }
                mTileArray[i][j].setDisable();
            }
        }
        mGame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        Board.getInfoFrame().dispose();
        JFrame loseFrame = new JFrame("You Lose!");
        loseFrame.setLocationRelativeTo(mGame);
        loseFrame.setSize(250, 150);
        loseFrame.setLayout(new GridLayout(2, 1));
        JLabel loseText = new JLabel("You lost! Would you like to play again?");
        JButton loseButton = new JButton("Replay?");
        loseFrame.add(loseText);
        loseFrame.add(loseButton);
        loseFrame.setResizable(false);
        loseFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        loseFrame.setAlwaysOnTop(true);
        loseFrame.setVisible(true);
        loseButton.addActionListener(e -> {
            mGame.dispose();
            loseFrame.dispose();
            Board newgame = new Board(mNumCols, mNumRows, mNumMines);
        });
        loseFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mGame.dispose();
                Menu.open();
            }
        });

    }

    /*gameWin()
     *
     * @ms.Pre-condition User has won, all tiles revealed, flags are placed
     * @ms.Post-condition Win window is revealed
     *
     * @see #gameOver()
     *
     * Displays all of the bombs and disables all of the buttons,
     * presents the user with a replay option.
     * */
    static void gameWin() {
        if (Board.getFlagCount() == 0) {
            boolean win = true;
            for (int i = 0; i < mNumRows; i++) {
                for (int j = 0; j < mNumCols; j++) {
                    if (mTileArray[i][j].getIsMine()) {
                        win &= mTileArray[i][j].getFlagged();
                    }
                }
            }
            if (win == true) {
                for (int i = 0; i < mNumRows; i++) {
                    for (int j = 0; j < mNumCols; j++) {
                        mTileArray[i][j].setDisable();
                    }
                }
                mGame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                Board.getInfoFrame().dispose();
                JFrame winFrame = new JFrame("You Win!");
                winFrame.setLocationRelativeTo(mGame);
                winFrame.setSize(250, 150);
                winFrame.setLayout(new GridLayout(2, 1));
                JLabel winText = new JLabel("You win! Would you like to play again?");
                JButton winButton = new JButton("Replay?");
                winFrame.add(winText);
                winFrame.add(winButton);
                winFrame.setResizable(false);
                winFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                winFrame.setAlwaysOnTop(true);
                winFrame.setVisible(true);
                winButton.addActionListener(e -> {
                    mGame.dispose();
                    winFrame.dispose();
                    Board newgame = new Board(mNumCols, mNumRows, mNumMines);
                });
                winFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        mGame.dispose();
                        Menu.open();
                    }
                });
            }
        }
    }

    //////////////////////////////
    /*HELPER METHODS            */
    //////////////////////////////

    /*isEndPossible()
     *
     * @return boolean isPossible, depends on if any buttons are still enabled
     *
     * @ms.Pre-condition called in (@link #Tile} #mouselistener to see if one has won
     * @ms.Post-condition the boolean returned checked if the game has been won
     *
     * @see #mouselistener
     *
     * Iterates through the board and checks
     * every tile that is not a mine, if every
     * tile that is not a mine is disabled as a result
     * of being clicked, the game may end. Only called
     * by setting a flag.
     * */
    static boolean isEndPossible() {
        boolean isPossible = true;
        for (int i = 0; i < mNumRows; i++) {
            for (int j = 0; j < mNumCols; j++) {
                if (!(mTileArray[i][j].getIsMine())) {
                    isPossible &= !(mTileArray[i][j].isEnabled());
                }
            }
        }
        return (isPossible);
    }

    /*openTile()
     * @param i coordinate to which tile needs to be opened
     * @param j coordinate to which tile needs to be opened
     *
     * @ms.Pre-condition User clicked on a tile and needs corresponding tiles to open as well
     * @ms.Post-condition Tile is left opened with corresponding tiles opened as well
     *
     * @see #setIsOpened()
     * @see #displaySurroundingMines()
     * @see #canOpen()
     *
     * If a tile is blank, check that the orthogonally adjacent
     * tiles are openable with canOpen(), if true recursively call
     * openTile() until tiles that are unopenable are encountered.
     * */
    static void openTile(int i, int j) {

        mTileArray[i][j].setIsOpened();
        mTileArray[i][j].displaySurroundingMines();

        if (mTileArray[i][j].getSurroundingMines() == 0) {
            int leftOne = i - 1;
            int rightOne = i + 1;
            int downOne = j - 1;
            int upOne = j + 1;

            if (leftOne >= 0 && mTileArray[leftOne][j].canOpen())
                openTile(leftOne, j);
            if (downOne >= 0 && mTileArray[i][downOne].canOpen())
                openTile(i, downOne);
            if (upOne < mNumCols && mTileArray[i][upOne].canOpen())
                openTile(i, upOne);
            if (rightOne < mNumRows && mTileArray[rightOne][j].canOpen())
                openTile(rightOne, j);
        }
    }

    /////////////////////////////////////
    /*Methods that initialize the board*/
    /////////////////////////////////////

    /*initBoard()
     *
     * @ms.Pre-condition called in constructor after using paramaters from {@link #Board}
     * @ms.Post-condition game board is initialized through mTileArray
     *
     * @see #placeMines()
     * @see #setRiskNum()
     *
     * calls placeMines() to randomly place mines throughout
     * the board and then calls setRiskNum() to set the mine
     * count of every tile.
     * */
    private void initBoard() {
        placeMines();
        setRiskNum();
    }

    /*placeMines()
     *
     * @ms.Pre-No guarantees are made before this function is called
     * @ms.Post-condition all mines are set into place
     *
     * @see #setMine()
     *
     * calls setMine() the number of times equal to the
     * number of mines
     * */
    private void placeMines() {
        for (int i = 0; i < mNumMines; i++) {
            setMine();
        }
    }

    /*setMine()
     * @Return void
     *
     * @ms.Pre-condition called number of mines times
     * @ms.Post-condition mine is set in a random tile
     *
     * @see #random
     *
     * sets a random x and y bound by the length of
     * the rows and columns then checks if a mine is present,
     * if a mine is present call setMine() again, eventually
     * coming off the stack when all mines are placed.
     * */
    private void setMine() {
        int x = random.nextInt(mNumRows);
        int y = random.nextInt(mNumCols);

        if (!mTileArray[x][y].getIsMine()) {
            mTileArray[x][y].setIsMine(true);
        } else {
            setMine();
        }
    }

    /*setRiskNum()
     * @Return void
     *
     * @ms.Pre-condition No guarantees are made before this function is called
     * @ms.Post-condition All of the tiles are given a mine risk number
     *
     * @see #getIsMine()
     *
     * checks the surrounding positions of a tile for a mine, if
     * a mine is present increment mineRisk, once all if statements
     * are checked called setSurroundingMines() and pass in mineRisk.
     * */
    private void setRiskNum() {
        for (int i = 0; i < mNumRows; i++) {
            int leftOne = i - 1;
            int rightOne = i + 1;

            for (int j = 0; j < mNumCols; j++) {
                int downOne = j - 1;
                int upOne = j + 1;
                int mineRisk = 0;

                /*
                 *Starts from bottom left of tile and checks in a clockwise pattern
                 */
                if (leftOne >= 0 && downOne >= 0 && mTileArray[leftOne][downOne].getIsMine()) {
                    mineRisk++;
                }
                if (leftOne >= 0 && mTileArray[leftOne][j].getIsMine()) {
                    mineRisk++;
                }
                if (leftOne >= 0 && upOne < mNumCols && mTileArray[leftOne][upOne].getIsMine()) {
                    mineRisk++;
                }
                if (upOne < mNumCols && mTileArray[i][upOne].getIsMine()) {
                    mineRisk++;
                }
                if (rightOne < mNumRows && upOne < mNumCols && mTileArray[rightOne][upOne].getIsMine()) {
                    mineRisk++;
                }
                if (rightOne < mNumRows && mTileArray[rightOne][j].getIsMine()) {
                    mineRisk++;
                }
                if (rightOne < mNumRows && downOne >= 0 && mTileArray[rightOne][downOne].getIsMine()) {
                    mineRisk++;
                }
                if (downOne >= 0 && mTileArray[i][downOne].getIsMine()) {
                    mineRisk++;
                }

                mTileArray[i][j].setSurroundingMines(mineRisk);
            }
        }
    }
}
