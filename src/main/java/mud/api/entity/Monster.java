package mud.api.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Monster {
    @Id
    private int id;
    private String name;
    private String look;
    private String actionOnRoad;
    private int level;
    private int hp;
    private int exp;
    private int money;
    @Transient
    private int[] maxhpTable = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    @Transient
    private int[] attackPowerTable = {58, 10, 16, 23, 28, 34, 40, 46, 51, 58};
    @Transient
    private int[] defencePowerTable = {7, 9, 12, 17, 23, 30, 37, 48, 58, 70};

    private Monster() {

    }

    public Monster(int id, String name, String look, String actionOnRoad, int level, int hp, int exp, int money) {
        this.id = id;
        this.name = name;
        this.look = look;
        this.actionOnRoad = actionOnRoad;
        this.level = level;
        this.hp = hp;
        this.exp = exp;
        this.money = money;
    }

    public Monster(Monster monster) {
        this(monster.getId(), monster.getName(), monster.getLook(), monster.getActionOnRoad(),
                monster.getLevel(), monster.getHp(), monster.getExp(), monster.getMoney());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLook() {
        return look;
    }

    public void setLook(String look) {
        this.look = look;
    }

    public String getActionOnRoad() {
        return actionOnRoad;
    }

    public void setActionOnRoad(String actionOnRoad) {
        this.actionOnRoad = actionOnRoad;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMaxHp() {
        return maxhpTable[getLevel() - 1];
    }

    public int getAttackPower() {
        return attackPowerTable[getLevel() - 1];
    }

    public int getDefencePower() {
        return defencePowerTable[getLevel() - 1];
    }
}
