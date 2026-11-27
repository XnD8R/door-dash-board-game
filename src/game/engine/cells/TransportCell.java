package game.engine.cells;

import game.engine.monsters.Monster;

public abstract class TransportCell extends Cell {

    private int effect;

    public TransportCell(String name, int effect) {
        super(name);
        this.effect = effect;
    }

    public void transport(Monster monster) {
        int newPos = monster.getPosition() + effect;
        if (newPos < 0) {
            newPos = 0;
        } else {
            newPos = newPos % 100;
        }
        monster.setPosition(newPos);
    }

    @Override
    public void onLand(Monster landingMonster, Monster opponentMonster) {
        super.onLand(landingMonster, opponentMonster);
        transport(landingMonster);
    }

    public int getEffect() { return effect; }
}
