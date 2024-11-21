package mud.api.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "`character`")
public class Character {
    @Id
    private int id;
    private String name;
    private String account;
    private String job;
    private int exp;
    private int money;
    private int head;
    private int body;
    private int pants;
    private int boots;
    private int weapon;
    private int shield;
    @Column(name = "inventory")
    private String inventoryStr;
    private String skills;

    // TEMP VALUE, can be calculated
    @Transient
    private int level;
    @Transient
    private int roomId;
    @Transient
    private int hp;
    @Transient
    private int mp;
    @Transient
    private Map<Item, Integer> inventory = new HashMap<>();
    @Transient
    private Equipment equipment = new Equipment();
    @Transient
    private List<Skill> skillList = new ArrayList<>();

    // Default
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public int getPants() {
        return pants;
    }

    public void setPants(int pants) {
        this.pants = pants;
    }

    public int getBoots() {
        return boots;
    }

    public void setBoots(int boots) {
        this.boots = boots;
    }

    public int getWeapon() {
        return weapon;
    }

    public void setWeapon(int weapon) {
        this.weapon = weapon;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public String getInventoryStr() {
        return inventoryStr;
    }

    public void setInventoryStr(String inventoryStr) {
        this.inventoryStr = inventoryStr;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    // SET TRANSIENT ENCAPSULATION
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public Map<Item, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<Item, Integer> inventory) {
        this.inventory = inventory;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }

    @Transient
    public void addItem(Item newItem, Integer count) {
        if (newItem.getItemtype().equals("돈")) {
            setMoney(getMoney() + count);

            return;
        }

        boolean exist = false;
        Iterator iterator = inventory.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Item item = (Item) entry.getKey();
            if (item.getItemname().equals(newItem.getItemname())) {
                exist = true;
                inventory.put(item, inventory.get(item) + count);
            }
        }

        if (exist == false) {
            inventory.put(newItem, count);
        }
    }
}
