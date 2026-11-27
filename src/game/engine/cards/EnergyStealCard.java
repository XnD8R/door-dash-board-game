package game.engine.cards;

import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class EnergyStealCard extends Card implements CanisterModifier {

    private final int energy;

    public EnergyStealCard(String name, String description, int rarity, int energy) {
        super(name, description, rarity, true);
        this.energy = energy;
    }

    @Override
    public void modifyCanisterEnergy(Monster monster, int canisterValue) {
        monster.alterEnergy(canisterValue);
    }

    @Override
    public void performAction(Monster player, Monster opponent) {
        int stolen = Math.min(energy, opponent.getEnergy());
        int opponentBefore = opponent.getEnergy();
        modifyCanisterEnergy(opponent, -stolen);
        int actuallyStolen = (opponent.getEnergy() != opponentBefore) ? stolen : 0;
        modifyCanisterEnergy(player, actuallyStolen);
    }

    public int getEnergy() { return energy; }
}

/*
 * AI helped fix: player was gaining 2x stolen amount when opponent was Dynamo
 * Fix: player gains intended steal amount, not actual energy lost
 */