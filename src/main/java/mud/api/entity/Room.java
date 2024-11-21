package mud.api.entity;

import mud.api.manager.ItemManager;
import mud.api.manager.RoomManager;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Room {
    @Id
    private int id;
    private int east;
    private int west;
    private int south;
    private int north;
    private int up;
    private int down;
    private String roomname;
    private String description;
    private String items;
    private String npcs;
    private String monsters;
    private Integer quest;
    @Transient
    private List<Item> itemList = new ArrayList<>();
    @Transient
    private List<Monster> monsterList = new ArrayList<>();
    @Transient
    private List<Npc> npcList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEast() {
        return east;
    }

    public void setEast(int east) {
        this.east = east;
    }

    public int getWest() {
        return west;
    }

    public void setWest(int west) {
        this.west = west;
    }

    public int getSouth() {
        return south;
    }

    public void setSouth(int south) {
        this.south = south;
    }

    public int getNorth() {
        return north;
    }

    public void setNorth(int north) {
        this.north = north;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getNpcs() {
        return npcs;
    }

    public void setNpcs(String npcs) {
        this.npcs = npcs;
    }

    public String getMonsters() {
        return monsters;
    }

    public void setMonsters(String monsters) {
        this.monsters = monsters;
    }

    public void removeMoneter(Monster monster) {
        monsterList.remove(monster);
    }

    public Integer getQuest() {
        return quest;
    }

    public void setQuest(Integer quest) {
        this.quest = quest;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Monster> getMonsterList() {
        return monsterList;
    }

    public void setMonsterList(List<Monster> monsterList) {
        this.monsterList = monsterList;
    }

    public List<Npc> getNpcList() {
        return npcList;
    }

    public void setNpcList(List<Npc> npcList) {
        this.npcList = npcList;
    }





}
