package ch.bbw.zork;

public class Enemy {
    // Attributes
    private String enemyName;
    private double healthPoints;
    private double attackPower;

    // Constructors
    public Enemy(String enemyName, double healthPoints, double attackPower) {
        this.enemyName = enemyName;
        this.healthPoints = healthPoints;
        this.attackPower = attackPower;
    }

    // Default enemy - Critter
    public Enemy() {
        enemyName = "Critters";
        healthPoints = 1.0;
        attackPower = 1.0;
    }

    // Methods
    public String getEnemyName() {
        return enemyName;
    }

    public double getHealthPoints() {
        return healthPoints;
    }

    public double getAttackPower() {
        return attackPower;
    }

    public void minusHealthPoints(double hitPoint) {
        this.healthPoints -= hitPoint;
    }

    public boolean isDefeated() {
        return this.healthPoints <= 0;
    }
}
