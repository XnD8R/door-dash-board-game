package game.engine;
import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    private final Board              board;
    private final ArrayList<Monster> allMonsters;
    private final Monster            player;
    private final Monster            opponent;
    private Monster                  current;

    public Game(Role playerRole) throws IOException {
        board = new Board(DataLoader.readCards());
        allMonsters = DataLoader.readMonsters();
        player = selectRandomMonsterByRole(playerRole);
        Role opponentRole = (playerRole == Role.SCARER) ? Role.LAUGHER : Role.SCARER;
        opponent = selectRandomMonsterByRole(opponentRole);
        current = player;
        allMonsters.remove(player);
        allMonsters.remove(opponent);
        board.setStationedMonsters(allMonsters);
        board.initializeBoard(DataLoader.readCells());
    }

    private Monster selectRandomMonsterByRole(Role role) {
        ArrayList<Monster> filtered = new ArrayList<>();
        for (Monster m : allMonsters)
            if (m.getRole() == role) filtered.add(m);
        if (filtered.isEmpty()) return null;
        return filtered.get(new Random().nextInt(filtered.size()));
    }

    private Monster getCurrentOpponent() {
        return (current == player) ? opponent : player;
    }

    private int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    public void usePowerup() throws OutOfEnergyException {
        if (current.getEnergy() < Constants.POWERUP_COST)
            throw new OutOfEnergyException();
        current.setEnergy(current.getEnergy() - Constants.POWERUP_COST);
        current.executePowerupEffect(getCurrentOpponent());
    }

    public void playTurn() throws InvalidMoveException {
        if (current.isFrozen()) {
            current.setFrozen(false);
            switchTurn();
        } else {
            int roll = rollDice();
            board.moveMonster(current, roll, getCurrentOpponent());
            switchTurn();
        }
    }

    private void switchTurn() {
        current = getCurrentOpponent();
    }

    private boolean checkWinCondition(Monster monster) {
        return monster.getPosition() == Constants.WINNING_POSITION
                && monster.getEnergy() >= Constants.WINNING_ENERGY;
    }

    public Monster getWinner() {
        if (checkWinCondition(player))   return player;
        if (checkWinCondition(opponent)) return opponent;
        return null;
    }

    public Board              getBoard()      { return board; }
    public ArrayList<Monster> getAllMonsters() { return allMonsters; }
    public Monster            getPlayer()     { return player; }
    public Monster            getOpponent()   { return opponent; }
    public Monster            getCurrent()    { return current; }
    public void               setCurrent(Monster m) { current = m; }
}
