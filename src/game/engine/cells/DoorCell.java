package game.engine.cells;

import game.engine.Board;
import game.engine.Role;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class DoorCell extends Cell implements CanisterModifier {

    private final Role role;
    private final int  energy;
    private boolean    activated;

    public DoorCell(String name, Role role, int energy) {
        super(name);
        this.role      = role;
        this.energy    = energy;
        this.activated = false;
    }


    @Override
    public void modifyCanisterEnergy(Monster monster, int canisterValue) {
        if (monster.getRole() == this.role) {
            monster.alterEnergy(canisterValue);   
        } else {
            monster.alterEnergy(-canisterValue);  
        }
    }

    @Override
    public void onLand(Monster landingMonster, Monster opponentMonster) {
        super.onLand(landingMonster, opponentMonster);
        if (activated) return;

        boolean energyChanged = false;

       
        boolean differentRole = landingMonster.getRole() != this.role;
        boolean shieldWillBlock = differentRole && landingMonster.isShielded();

        
        int before = landingMonster.getEnergy();
        modifyCanisterEnergy(landingMonster, energy);
        if (landingMonster.getEnergy() != before) energyChanged = true;

        
        if (!shieldWillBlock) {
            for (Monster stationed : Board.getStationedMonsters()) {
                if (stationed.getRole() == landingMonster.getRole()) {
                    int sBefore = stationed.getEnergy();
                    modifyCanisterEnergy(stationed, energy);
                    if (stationed.getEnergy() != sBefore) energyChanged = true;
                }
            }
        }

        if (energyChanged) activated = true;
    }

    public Role    getRole()      { return role; }
    
    public int     getEnergy()    { return energy; }
    
    public boolean isActivated()  { return activated; }
    
    public void    setActivated(boolean activated) { this.activated = activated; }
}
