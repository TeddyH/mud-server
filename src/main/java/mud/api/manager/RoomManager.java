package mud.api.manager;

import mud.api.common.ConfigManager;
import mud.api.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RoomManager {
    private static final Logger logger = LoggerFactory.getLogger(RoomManager.class);
    // set variables
    private static Map<Integer, Room> roomMap = new HashMap<>();
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    ItemManager itemManager;
    @Autowired
    NpcManager npcManager;
    @Autowired
    MonsterManager monsterManager;
    @Autowired
    ConfigManager configManager;
    @Autowired
    MessageManager messageManager;

    public static Room getRoom(int roomId) {
        return roomMap.get(roomId);
    }

    public static int getRoomCount() {
        return roomMap.size();
    }

    public static String getAvailableDirections(int roomId) {
        Room room = getRoom(roomId);
        String ret = "<br><br>이동 가능한 방향은 다음과 같습니다.";
        if (room.getEast() != 0) {
            ret += "<br>[동] " + getRoom(room.getEast()).getRoomname();
        }
        if (room.getWest() != 0) {
            ret += "<br>[서] " + getRoom(room.getWest()).getRoomname();
        }
        if (room.getSouth() != 0) {
            ret += "<br>[남] " + getRoom(room.getSouth()).getRoomname();
        }
        if (room.getNorth() != 0) {
            ret += "<br>[북] " + getRoom(room.getNorth()).getRoomname();
        }
        if (room.getUp() != 0) {
            ret += "<br>[위] " + getRoom(room.getUp()).getRoomname();
        }
        if (room.getDown() != 0) {
            ret += "<br>[아래] " + getRoom(room.getDown()).getRoomname();
        }
        ret += "<br>";

        return ret;
    }

    @PostConstruct
    @Scheduled(cron = "0 */5 * * * *")
    public void run() {
        initializeAllRoom();
        messageManager.broadcast("<br>어디선가 바람이 불어옵니다.");
    }

    private void initializeAllRoom() {
        List<Room> roomList = roomRepository.findAll();
        for (Room room : roomList) {
            // Set itemList
            if (room.getItems() != null) {
                String[] items = room.getItems().split(",");
                List<Item> itemList = new ArrayList<>();
                for (String itemId : items) {
                    Item item = itemManager.getItem(Integer.parseInt(itemId));
                    itemList.add(itemManager.getItem(Integer.parseInt(itemId)));
                }
                room.setItemList(itemList);
            }

            // Set NpcList
            if (room.getNpcs() != null) {
                String[] npcs = room.getNpcs().split(",");
                List<Npc> npcList = new ArrayList<>();
                for (String npcId : npcs) {
                    npcList.add(npcManager.getNpc(Integer.parseInt(npcId)));
                }
                room.setNpcList(npcList);
            }

            // Set monsterList
            if (room.getMonsters() != null) {
                String[] monsters = room.getMonsters().split(",");
                List<Monster> monsterList = new ArrayList<>();
                for (String monster : monsters) {

                    if (monster.trim() != "") {
                        monsterList.add(monsterManager.getMonster(Integer.parseInt(monster)));
                    }
                }
                room.setMonsterList(monsterList);
            }

            roomMap.put(room.getId(), room);
        }

//        logger.info("RoomManager Initialized. Load: " + roomMap.size());
    }

    public String getItemsLook(int roomId) {
        Room room = getRoom(roomId);
        String message = "";
        if (room.getItemList().size() > 0) {
            for (Item item : room.getItemList()) {
                message += "<br>" + item.getLook();
            }
        }
        return message;
    }

    public String getNpcsLook(int roomId) {
        Room room = getRoom(roomId);
        String message = "";
        if (room.getNpcList().size() > 0) {
            for (Npc npc : room.getNpcList()) {
                message += "<br>" + npc.getLook();
            }
        }
        return message;
    }

    public String getMonstersLook(int roomId) {
        Room room = getRoom(roomId);
        String message = "";
        if (room.getMonsterList().size() > 0) {
            for (Monster monster : room.getMonsterList()) {
                message += "<br>" + monster.getLook();
            }
        }
        return message;
    }

    public List<Monster> getMonsterList(int roomId) {
        Room room = getRoom(roomId);
        return room.getMonsterList();
    }

    public List<Npc> getNpcList(int roomId) {
        Room room = getRoom(roomId);
        return room.getNpcList();
    }

    public String getObjectMessage(int roomId) {
        String message = "";
        if (getNpcsLook(roomId) != null) {
            message += getNpcsLook(roomId);
        }

        if (getMonstersLook(roomId) != null) {
            message += getMonstersLook(roomId);
        }

        if (getItemsLook(roomId) != null) {
            message += getItemsLook(roomId);
        }

        return message;
    }

    public List<String> getActions(int roomId) {
        Room room = getRoom(roomId);
        List<String> actionList = new ArrayList<>();

        if (room.getItemList().size() > 0) {
            for (Item item : room.getItemList()) {
                actionList.add(item.getActiononroad());
            }
        }

        if (room.getNpcList().size() > 0) {
            for (Npc npc : room.getNpcList()) {
                actionList.add(npc.getActionOnRoad());
            }
        }

        if (room.getMonsterList().size() > 0) {
            for (Monster monster : room.getMonsterList()) {
                actionList.add(monster.getActionOnRoad());
            }
        }

        return actionList;
    }

    public String getActionsMessage(int roomId) {
        String message = "";
        for (String action : getActions(roomId)) {
            message += action + ";";
        }

        return message;
    }

    public boolean checkItemInRoom(int roomId, String itemName) {
        Room room = getRoom(roomId);
        if (room.getItemList().size() == 0) {
            return false;
        }

        for (Item itemr : room.getItemList()) {
            if (itemr.getItemname().equals(itemName)) {
                return true;
            }
        }

        return false;
    }

    public boolean removeItem(int roomId, String itemName) {
        Room room = getRoom(roomId);
        if (room.getItemList().size() == 0) {
            return false;
        }

        for (Item itemr : room.getItemList()) {
            if (itemr.getItemname().equals(itemName)) {
                return room.getItemList().remove(itemr);
            }
        }

        return false;
    }

    public void removeMonster(int roomId, Monster monster) {
        Room room = getRoom(roomId);
        room.removeMoneter(monster);
    }
}
