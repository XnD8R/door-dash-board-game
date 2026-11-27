package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;

public class MultiTasker extends Monster {

    private int     normalSpeedTurns;
    private boolean constructed = false;

    public MultiTasker(String name, String description, Role role, int energy) {
        super(name, description, role, energy);
        constructed = true;
    }

    @Override
    public void move(int distance) {
        if (normalSpeedTurns > 0) {
            super.move(distance);
            normalSpeedTurns--;
        } else {
            super.move(distance / 2);
        }
    }

    @Override
    public void setEnergy(int energy) {
        if (!constructed) {
            super.setEnergy(energy);
            return;
        }
        int delta = energy - getEnergy();
        super.setEnergy(getEnergy() + delta + Constants.MULTITASKER_BONUS);
    }

    @Override
    public void executePowerupEffect(Monster opponentMonster) {
        normalSpeedTurns = 2;
    }

    public int  getNormalSpeedTurns()                      { return normalSpeedTurns; }
    public void setNormalSpeedTurns(int normalSpeedTurns)  { this.normalSpeedTurns = normalSpeedTurns; }
}
