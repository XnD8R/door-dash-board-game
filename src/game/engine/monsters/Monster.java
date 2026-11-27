package game.engine.monsters;

import game.engine.Constants;

import game.engine.Role;

public abstract class Monster implements Comparable<Monster> {

    private final String name;
    private final String description;
    private Role role;
    private final Role originalRole;
    private int energy;
    private int position;
    private boolean frozen;
    private boolean shielded;
    private int confusionTurns;

    public Monster(String name, String description, Role originalRole, int energy) {
        this.name = name;
        this.description = description;
        this.originalRole = originalRole;
        this.role = originalRole;
        this.energy = energy;
        this.position = 0;
        this.confusionTurns = 0;
        this.frozen = false;
        this.shielded = false;
    }

    public int compareTo(Monster o) {
        return this.position - o.position;
    }

    public String getName() { return name; }
    
    public String getDescription() { return description; }
    
    public Role getRole() { return role; }
    
    public void setRole(Role role) { this.role = role; }
    
    public Role getOriginalRole() { return originalRole; }
    
    public int getEnergy() { return energy; }
    
    public void setEnergy(int energy) {
        if (energy < 0) this.energy = 0;
        else this.energy = energy;
    }

    public int getPosition() { return position; }
    public void setPosition(int position) {
        if (position < 0) this.position = 0;
        else this.position = position % Constants.BOARD_SIZE; 
    }

    public boolean isFrozen() { return frozen; }
    
    public void setFrozen(boolean frozen) { this.frozen = frozen; }

    public boolean isShielded() { return shielded; }
    
    public void setShielded(boolean shielded) { this.shielded = shielded; }

    public int getConfusionTurns() { return confusionTurns; }
    
    public void setConfusionTurns(int confusionTurns) { this.confusionTurns = confusionTurns; }
 
    
    public abstract void executePowerupEffect(Monster opponentMonster);
    public boolean isConfused() {
        return confusionTurns > 0;
    }


    public void move(int distance) {
        int newPos = position + distance;
        if (newPos < 0) {
            newPos = 0;        
        } else {
            newPos = newPos % 100; 
        }
        position = newPos;
    }

    public final void alterEnergy(int energy) {
        if (isShielded() && energy < 0) {
            setShielded(false); 
        } else {
            setEnergy(getEnergy() + energy);
        }
    }

    public void decrementConfusion() {
        if (confusionTurns > 0) {
            confusionTurns--;
            if (confusionTurns == 0) {
                role = originalRole;
            }
        }
    }
}
