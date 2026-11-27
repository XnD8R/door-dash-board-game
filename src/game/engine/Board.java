package game.engine;

import game.engine.cards.Card;
import game.engine.cells.*;
import game.engine.exceptions.InvalidMoveException;
import game.engine.monsters.Monster;
import java.util.ArrayList;
import java.util.Collections;

public class Board {

    private Cell[][] boardCells;
    private static ArrayList<Monster> stationedMonsters = new ArrayList<>();
    private static ArrayList<Card>    originalCards;
    public static ArrayList<Card>    cards;
    private static Card lastDrawnCard = null;

    public Board(ArrayList<Card> readCards) {
        boardCells        = new Cell[Constants.BOARD_ROWS][Constants.BOARD_COLS];
        stationedMonsters = new ArrayList<>();
        cards             = new ArrayList<>();
        originalCards     = readCards;
        setCardsByRarity();
        reloadCards();
    }

    private int[] indexToRowCol(int index) {
        int row = index / Constants.BOARD_COLS;
        int col;
        if (row % 2 == 0) {
            col = index % Constants.BOARD_COLS;
        } else {
            col = Constants.BOARD_COLS - 1 - (index % Constants.BOARD_COLS);
        }
        return new int[]{row, col};
    }

    private Cell getCell(int index) {
        int[] rc = indexToRowCol(index);
        return boardCells[rc[0]][rc[1]];
    }

    private void setCell(int index, Cell cell) {
        int[] rc = indexToRowCol(index);
        boardCells[rc[0]][rc[1]] = cell;
    }

    public void initializeBoard(ArrayList<Cell> specialCells) {
        ArrayList<DoorCell>          doors = new ArrayList<>();
        ArrayList<ConveyorBelt>      belts = new ArrayList<>();
        ArrayList<ContaminationSock> socks = new ArrayList<>();

        for (Cell c : specialCells) {
            if      (c instanceof ConveyorBelt)      belts.add((ConveyorBelt) c);
            else if (c instanceof ContaminationSock) socks.add((ContaminationSock) c);
            else if (c instanceof DoorCell)          doors.add((DoorCell) c);
        }

        int doorIndex = 0;
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            if (i % 2 == 0) {
                setCell(i, new Cell("Rest Cell " + i));
            } else {
                if (!doors.isEmpty()) {
                    setCell(i, doors.get(doorIndex % doors.size()));
                    doorIndex++;
                } else {
                    setCell(i, new Cell("Rest Cell " + i));
                }
            }
        }

        for (int idx : Constants.CARD_CELL_INDICES) {
            setCell(idx, new CardCell("Card Cell " + idx));
        }

        for (int i = 0; i < Constants.CONVEYOR_CELL_INDICES.length; i++) {
            if (i < belts.size()) setCell(Constants.CONVEYOR_CELL_INDICES[i], belts.get(i));
        }

        for (int i = 0; i < Constants.SOCK_CELL_INDICES.length; i++) {
            if (i < socks.size()) setCell(Constants.SOCK_CELL_INDICES[i], socks.get(i));
        }

        for (int i = 0; i < Constants.MONSTER_CELL_INDICES.length; i++) {
            int idx = Constants.MONSTER_CELL_INDICES[i];
            if (i < stationedMonsters.size()) {
                Monster m = stationedMonsters.get(i);
                m.setPosition(idx);
                setCell(idx, new MonsterCell(m.getName(), m));
            }
        }
    }

    private void setCardsByRarity() {
        ArrayList<Card> expanded = new ArrayList<>();
        for (Card card : originalCards)
            for (int i = 0; i < card.getRarity(); i++)
                expanded.add(card);
        originalCards = expanded;
        cards = new ArrayList<>(expanded);
    }

    public static void reloadCards() {
        cards = new ArrayList<>(originalCards);
        Collections.shuffle(cards);
    }

    public static Card drawCard() {
        if (cards.isEmpty()) reloadCards();
        lastDrawnCard = cards.remove(0);
        return lastDrawnCard;
    }

    public static Card getLastDrawnCard() {
        return lastDrawnCard;
    }

    public void moveMonster(Monster currentMonster, int roll, Monster opponentMonster)
            throws InvalidMoveException {

        int oldPosition = currentMonster.getPosition();
        currentMonster.move(roll);

        Cell landedCell = getCell(currentMonster.getPosition());
        landedCell.onLand(currentMonster, opponentMonster);

        if (currentMonster.getPosition() == opponentMonster.getPosition()) {
            currentMonster.setPosition(oldPosition);
            throw new InvalidMoveException();
        }

        if (currentMonster.isConfused()) currentMonster.decrementConfusion();

        updateMonsterPositions(currentMonster, opponentMonster);
    }

    private void updateMonsterPositions(Monster player, Monster opponent) {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            Cell c = getCell(i);
            if (c != null) c.setMonster(null);
        }
        getCell(player.getPosition()).setMonster(player);
        getCell(opponent.getPosition()).setMonster(opponent);
    }

    public Cell[][] getBoardCells()                            { return boardCells; }
    public static ArrayList<Monster> getStationedMonsters()    { return stationedMonsters; }
    public static void setStationedMonsters(ArrayList<Monster> list) { stationedMonsters = list; }
    public static ArrayList<Card> getOriginalCards()           { return originalCards; }
    public static ArrayList<Card> getCards()                   { return cards; }
    public static void setCards(ArrayList<Card> c)             { cards = c; }
}
/*
 * AI helped fix: MonsterCell name was hardcoded instead of using monster's name
 * AI helped fix: stationedMonsters was null before Board created
 * AI helped fix: moveMonster() was decrementing opponent confusion incorrectly
 * AI helped fix: setCardsByRarity() was not updating originalCards correctly
 */
//    Milestone 3
/*
 * AI helped add lastDrawnCard field to track the last drawn card
 * AI helped add getLastDrawnCard() getter for use in GameView
 * I verified this correctly returned the card drawn in CardCell.onLand()
 */