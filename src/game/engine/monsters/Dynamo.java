package game.engine.monsters;

import game.engine.Role;

public class Dynamo extends Monster {

    private boolean constructed = false;

    public Dynamo(String name, String description, Role role, int energy) {
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
        super.setEnergy(getEnergy() + 2 * delta);
    }

    @Override
    public void executePowerupEffect(Monster opponentMonster) {
        opponentMonster.setFrozen(true);
    }
}

/*
 * AI helped fix: constructor was doubling the initial energy
 * Fix: added boolean constructed flag so 2x passive only applies after construction
 */
