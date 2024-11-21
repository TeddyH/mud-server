package mud.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.*;

@Entity
public class Player {
    // DB VALUE
    @Id
    private int id;
    private String nickname;
    @Column(name = "class")
    private String job;
    private String password;
    private int exp;
    private int money;
    @Column(name = "equipment")
    private String equipmentStr;
    @Column(name = "inventory")
    private String inventoryStr;
    private String quests;
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
    @Transient
    private List<String> questList = new ArrayList<>();

    // CONSTRUCTOR

    // DB VALUE from here
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEquipmentStr() {
        return equipmentStr;
    }

    public void setEquipmentStr(String equipmentStr) {
        this.equipmentStr = equipmentStr;
    }

    public String getInventoryStr() {
        return inventoryStr;
    }

    public void setInventoryStr(String inventoryStr) {
        this.inventoryStr = inventoryStr;
    }

    public String getQuests() {
        return quests;
    }

    public void setQuests(String quests) {
        this.quests = quests;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    // TEMP VALUE from here
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

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Map<Item, Integer> getInventory() {
        return inventory;
    }

    // FUNCTIONS from here
    @Transient
    public void setInventory(Map<Item, Integer> inventory) {
        this.inventory = inventory;
    }

    @Transient
    public String getInventoryCommand() {
        String ret = "";
        if (inventory.size() > 0) {
            Iterator iterator = inventory.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Item item = (Item) entry.getKey();
                ret += item.getActionininventory() + "&&";
            }
        }

        return ret;
    }

    @Transient
    public String getSkilSet() {
        if (skillList.size() == 0) {
            return "아직 배운 것이 없습니다.";
        } else {
            return "아직 배운 것이 없습니다.";
        }
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

    @Transient
    public void printInventory() {
        Iterator iterator = inventory.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Item itemInv = (Item) entry.getKey();
            System.out.println("PrintInventory: " + itemInv.getItemname() + ", " + entry.getValue());
        }
    }

    public List<String> getQuestList() {
        return questList;
    }

    public void setQuestList(List<String> questList) {
        this.questList = questList;
    }

    @Transient
    public void updateQuestProceed(String qsId) {
        boolean found = false;
        ListIterator<String> iterator = questList.listIterator();
        while (iterator.hasNext()) {
            int indexDid = iterator.nextIndex();
            String questDid = iterator.next();
            System.out.println("questDid: " + questDid);
            if (questDid.split("-")[0].equals(qsId.split("-")[0])) {
                found = true;
                System.out.println("set: " + indexDid + "/" + qsId);
                questList.set(indexDid, qsId);
            }
        }
        if (found == false) {
            questList.add(qsId);
        }
    }
}
