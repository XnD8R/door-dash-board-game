package game.engine.monsters;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Role;

public class Schemer extends Monster {

    private boolean constructed = false;

    public Schemer(String name, String description, Role role, int energy) {
        super(name, description, role, energy);
        constructed = true;
    }

    @Override
    public void setEnergy(int energy) {
        if (!constructed) {
            super.setEnergy(energy);
            return;
        }
        int delta = energy - getEnergy();
        super.setEnergy(getEnergy() + delta + Constants.SCHEMER_STEAL);
    }

    private int stealEnergyFrom(Monster target) {
        int steal = Math.min(Constants.SCHEMER_STEAL, target.getEnergy());
        target.setEnergy(target.getEnergy() - steal);
        return steal;
    }

    @Override
    public void executePowerupEffect(Monster opponentMonster) {
        int totalStolen = stealEnergyFrom(opponentMonster);
        for (Monster stationed : Board.getStationedMonsters()) {
            totalStolen += stealEnergyFrom(stationed);
        }
        setEnergy(getEnergy() + totalStolen);
    }
}
