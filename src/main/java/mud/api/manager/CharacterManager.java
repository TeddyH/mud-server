package mud.api.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mud.api.common.ConfigManager;
import mud.api.common.Constants;
import mud.api.common.StringUtils;
import mud.api.entity.Character;
import mud.api.entity.*;
import mud.api.manager.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CharacterManager {
    private static final Logger logger = LoggerFactory.getLogger(CharacterManager.class);
    //game variables
    public int maxHp = 100;
    public int maxMp = 100;
    @Autowired
    CharacterRepository characterRepository;
    @Autowired
    ItemManager itemManager;
    @Autowired
    RoomManager roomManager;
    @Autowired
    NpcManager npcManager;
    @Autowired
    MessageManager messageManager;
    @Autowired
    ConfigManager configManager;
    //set variables
    private Map<String, Character> characterMap = new HashMap<>();
    private int[] levelTable = {0, 10, 50, 100, 200, 400};
    private int[] attackPowerTable = {8, 12, 15, 21, 27, 34, 41, 52, 62, 74};
    private int[] defencePowerTable = {4, 5, 11, 17, 22, 28, 33, 39, 44, 49};

    @PostConstruct
    public void run() {
        initialize();
    }

    private void initialize() {
        //Player는 변하는 값이므로 필요할때마다 db에서 가져온다.
        List<Character> characterList = characterRepository.findAll();
        for (Character character : characterList) {
            // set default
            character.setHp(getMaxHp());
            character.setLevel(1);

            for (int i = levelTable.length - 1; i > 0; i--) {
                // SET LEVEL
                if (character.getExp() > levelTable[i]) {
                    character.setLevel(i);
                    break;
                }

                // SET EQUIPMENT
                Equipment equipment = new Equipment();
                // 머리
                if (character.getHead() != 0) {
                    equipment.setHead(itemManager.getItem(character.getHead()));
                }
                // 몸통
                if (character.getBody() != 0) {
                    equipment.setBody(itemManager.getItem(character.getBody()));
                }
                // 바지
                if (character.getPants() != 0) {
                    equipment.setPants(itemManager.getItem(character.getPants()));
                }
                // 신발
                if (character.getBoots() != 0) {
                    equipment.setBoots(itemManager.getItem(character.getBoots()));
                }
                // 무기
                if (character.getWeapon() != 0) {
                    equipment.setWeapon(itemManager.getItem(character.getWeapon()));
                }
                // 방패
                if (character.getShield() != 0) {
                    equipment.setShield(itemManager.getItem(character.getShield()));
                }

                // SET INVENTORY
                if (character.getInventoryStr() != null) {
                    Map<Item, Integer> inventory = new HashMap<>();
                    String[] items = character.getInventoryStr().split(",");
                    for (String itemInfo : items) {
                        Item item = itemManager.getItem(Integer.parseInt(itemInfo.split("x")[0]));
                        inventory.put(item, Integer.parseInt(itemInfo.split("x")[1]));
                    }
                    character.setInventory(inventory);
                }
            }
            characterMap.put(character.getName(), character);
        }

        messageManager.updateCharacterList(characterList);

        logger.info("CharacterManager Initialized. Load: " + characterMap.size());
    }

    public Character createCharacter(String name, String job, String account) {
        Character check = characterRepository.findByName(name);

        if (check == null) {
            characterRepository.createCharacter(name, job, account);
            Character character = characterRepository.findByName(name);

            characterMap.put(name, character);

            return character;
        }

        return null;
    }

    public List<Character> getAllCharacters() {
        List<Character> characterList = new ArrayList<>();
        Iterator iterator = characterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Character character = (Character) entry.getValue();
            characterList.add(character);
        }

        return characterList;
    }

    public List<Character> getCharacters(String userid) {
        List<Character> characterList = new ArrayList<>();
        Iterator iterator = characterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Character character = (Character) entry.getValue();
            if (character.getAccount().equals(userid)) {
                characterList.add(character);
            }
        }

        return characterList;
    }

    public Character setCharacterJob(String name, String job) {
        Character character = characterMap.get(name);
        character.setJob(job);
        character.setHp(maxHp);
        character.setExp(0);
        character.setLevel(1);
        character.setMoney(100);
        characterRepository.save(character);
        characterMap.put(name, character);

        return character;
    }

    public Character getCharacter(String name) {
        return characterMap.get(name);
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public int getRoomId(String nickName) {
        Character character = characterMap.get(nickName);
        return character.getRoomId();
    }

    public int getAttackPower(String name) {
        Character character = characterMap.get(name);
        int attackPower = attackPowerTable[character.getLevel() - 1];
        if (character.getWeapon() != 0) {
            attackPower += character.getEquipment().getWeapon().getEffect();
        }
        return attackPower;
    }

    public int getDefencePower(Character character) {
        return defencePowerTable[character.getLevel() - 1];
    }

    public void addExp(String name, int exp) {
        Character character = characterMap.get(name);
        character.setExp(character.getExp() + exp);

        if (character.getExp() > levelTable[character.getLevel()]) {
            character.setLevel(character.getLevel() + 1);
            messageManager.put(new Message(character, Constants.MESSAGE_TYPE.LEVEL_UP, "<<< 레벨이 올랐습니다!!! >>>"));
            character.setHp(getMaxHp());
            character.setMp(getMaxMp());
        }
        characterMap.put(name, character);
    }

    public void addMoney(String name, int money) {
        Character character = characterMap.get(name);
        character.setMoney(character.getMoney() + money);
        characterMap.put(name, character);
    }

    public String getStatus(String name) {
        Character character = characterMap.get(name);
        String statusDesc = "===== 현재 상태 =====";
        statusDesc += "<br>[이름] : " + name;
        statusDesc += "<br>[직업] : " + character.getJob();
        statusDesc += "<br>[레벨] : " + character.getLevel();
        statusDesc += "<br>[경험치] : " + character.getExp();

        return statusDesc;
    }

    public String getEquipmentStatus(String name) {
        Character character = characterMap.get(name);
        String statusEquipment = "==== 착용중인 장비 ====";
        if (character.getEquipment().getHead() != null) {
            statusEquipment += "<br>[머리] : " + character.getEquipment().getHead().getItemname();
        }

        if (character.getEquipment().getBody() != null) {
            statusEquipment += "<br>[몸] : " + character.getEquipment().getBody().getItemname();
        }

        if (character.getEquipment().getWeapon() != null) {
            statusEquipment += "<br>[오른손] : " + character.getEquipment().getWeapon().getItemname();
        }

        return statusEquipment;
    }

    public String getEquipmentReleaseCommand(String name) {
        Character character = characterMap.get(name);
        String releaseCommand = "";
        if (character.getEquipment().getHead() != null) {
            releaseCommand += character.getEquipment().getHead().getItemname() + " 벗어&&";
        }

        if (character.getEquipment().getBody() != null) {
            releaseCommand += character.getEquipment().getBody().getItemname() + " 벗어&&";
        }

        if (character.getEquipment().getWeapon() != null) {
            releaseCommand += character.getEquipment().getWeapon().getItemname() + " 벗어&&";
        }

        return releaseCommand;
    }

    public String getInventory(String name) {
        Character character = characterMap.get(name);
        logger.debug("getInventory " + name + " " + character.getInventory().size());
        if (character.getInventory().size() == 0) {
            return "가방에 아무것도 없습니다.";
        } else {
            String retInventory = "";
            Iterator iterator = character.getInventory().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Item item = (Item) entry.getKey();
                retInventory += "<br>" + item.getItemname() + "(" + entry.getValue() + "개): " + item.getDescription();
            }

            return retInventory;
        }
    }

    public String getInventoryCommand(String name) {
        Character character = characterMap.get(name);
        String ret = "";
        if (character.getInventory().size() > 0) {
            Iterator iterator = character.getInventory().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Item item = (Item) entry.getKey();
                ret += item.getActionininventory() + "&&";
            }
        }

        return ret;
    }

    public void addItem(String name, String itemName) {
        Character character = characterMap.get(name);
        boolean exist = false;
        Iterator iterator = character.getInventory().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Item item = (Item) entry.getKey();
            if (item.getItemname().equals(itemName)) {
                exist = true;
                character.getInventory().put(item, character.getInventory().get(item) + 1);
            }
        }

        if (exist == false) {
            Item item = itemManager.getItem(itemName);
            character.getInventory().put(item, 1);
        }

        characterMap.put(name, character);
    }

    public String pickUpItem(String name, String itemName) {
        Character character = getCharacter(name);
        if (roomManager.checkItemInRoom(character.getRoomId(), itemName)) {
            roomManager.removeItem(character.getRoomId(), itemName);
            addItem(name, itemName);

            messageManager.broadcastInRoom(character, String.format("<br> %s %s 주웠습니다.",
                    StringUtils.getPostWord(name, "이", "가"),
                    StringUtils.getPostWord(itemName, "을", "를")));

            return String.format("당신이 %s 주웠습니다.", StringUtils.getPostWord(itemName, "을", "를"));
        } else {
            return String.format("그런 것이 없습니다.");
        }
    }

    public String getShopList(String name) {
        Character character = getCharacter(name);
        int roomId = character.getRoomId();
        List<Npc> npcList = roomManager.getNpcList(roomId);

        String ret = "";
        for (Npc npc : npcList) {
            ret += npcManager.getSellingList(npc);
        }

        return ret;
    }

    public String getShopCommandList(String name) {
        Character character = getCharacter(name);
        int roomId = character.getRoomId();
        List<Npc> npcList = roomManager.getNpcList(roomId);

        String ret = "";
        for (Npc npc : npcList) {
            ret += npcManager.getSellingCommandList(npc);
        }

        return ret;
    }

    public JsonNode buyItem(String name, String itemName) {
        ObjectNode objectNode = Constants.MAPPER.createObjectNode();

        Character character = getCharacter(name);
        Item item = itemManager.getItem(itemName);

        if (character.getMoney() >= item.getPrice()) {
            character.setMoney(character.getMoney() - item.getPrice());
            character.addItem(item, 1);
            objectNode.put(Constants.MESSAGE_TYPE.SNACK,
                    itemName + "을 샀습니다.");
            objectNode.put(Constants.MESSAGE_TYPE.TITLE2,
                    character.getHp() + "% " + character.getMoney() + "R");
            objectNode.put(Constants.MESSAGE_TYPE.MONEY, character.getMoney());

            messageManager.broadcastInRoom(character, String.format("<br> %s %s 샀습니다.",
                    StringUtils.getPostWord(name, "이", "가"),
                    StringUtils.getPostWord(itemName, "을", "를")));
        } else {
            objectNode.put(Constants.MESSAGE_TYPE.SNACK,
                    itemName + "을 사기에는 돈이 부족합니다.");
        }

        characterMap.put(name, character);

        return objectNode;
    }

    public String eatItem(String name, String itemName) {
        Character character = getCharacter(name);
        Item item = itemManager.getItem(itemName);

        if (!checkHasItem(name, itemName)) {
            return "가방 속에 " + StringUtils.getPostWord(itemName, "이", "가") + " 없습니다.";
        }

        removeItemFromInventory(name, item);

        String retMessage = null;
        switch (item.getItemtype()) {
            case "음료":
                if (item.getEffecttype().equals("HP")) {
                    if (character.getHp() + item.getEffect() > getMaxHp()) {
                        character.setHp(getMaxHp());
                    } else {
                        character.setHp(character.getHp() + item.getEffect());
                    }
                }
                retMessage = StringUtils.getPostWord(itemName, "을", "를") + " 마셨습니다. 갈증이 가십니다.";
                break;
        }

        messageManager.put(new Message(character, Constants.MESSAGE_TYPE.BODY, getBody(character)));
        characterMap.put(name, character);

        return retMessage;
    }

    public boolean checkHasItem(String name, String itemName) {
        Character character = getCharacter(name);
        Item item = itemManager.getItem(itemName);

        if (character.getInventory().get(item) != null) {
            return true;
        }

        return false;
    }

    public void removeItemFromInventory(String name, Item item) {
        Character character = getCharacter(name);

        if (character.getInventory().get(item) > 1) {
            character.getInventory().put(item, character.getInventory().get(item) - 1);
        } else {
            character.getInventory().remove(item);
        }
        characterMap.put(name, character);
    }

    public JsonNode wearItem(String name, String itemName) {
        ObjectNode objectNode = Constants.MAPPER.createObjectNode();

        Character character = getCharacter(name);
        Item item = itemManager.getItem(itemName);

        switch (item.getItemtype()) {
            case "무기":
                if (character.getEquipment().getWeapon() != null) {
                    objectNode.put(Constants.MESSAGE_TYPE.SNACK,
                            StringUtils.getPostWord(itemName, "을", "를") + " 착용하기 전에 "
                                    + StringUtils.getPostWord(character.getEquipment().getWeapon().getItemname(),
                                    "을", "를") + " 해제해야 합니다.");
                } else {
                    character.getEquipment().setWeapon(item);
                    removeItemFromInventory(name, item);

                    objectNode.put(Constants.MESSAGE_TYPE.SNACK, "당신은 " +
                            StringUtils.getPostWord(itemName, "을", "를") + " 오른손에 쥐었습니다.");

                    messageManager.broadcastInRoom(character, String.format("<br>%s %s 오른손에 쥐었습니다.",
                            StringUtils.getPostWord(character.getName(), "이", "가"),
                            StringUtils.getPostWord(itemName, "을", "를")));

                }

                break;
            case "갑옷":
                if (character.getEquipment().getBody() != null) {
                    objectNode.put(Constants.MESSAGE_TYPE.SNACK,
                            StringUtils.getPostWord(itemName, "을", "를") + " 착용하기 전에 " +
                                    StringUtils.getPostWord(character.getEquipment().getBody().getItemname(),
                                            "을", "를") + " 해제해야 합니다.");
                } else {
                    character.getEquipment().setBody(item);
                    removeItemFromInventory(name, item);

                    objectNode.put(Constants.MESSAGE_TYPE.SNACK,
                            "당신은 " + StringUtils.getPostWord(itemName, "을", "를") + " 입었습니다.");

                    messageManager.broadcastInRoom(character, String.format("<br>%s %s 입었습니다.",
                            StringUtils.getPostWord(character.getName(), "이", "가"),
                            StringUtils.getPostWord(itemName, "을", "를")));
                }
                break;
        }

        characterMap.put(name, character);

        return objectNode;
    }

    public JsonNode releaseItem(String name, String itemName) {
        ObjectNode objectNode = Constants.MAPPER.createObjectNode();

        Character character = getCharacter(name);
        Item item = itemManager.getItem(itemName);
        switch (item.getItemtype()) {
            case "무기":
                character.getEquipment().setWeapon(null);
                character.addItem(item, 1);
                objectNode.put(Constants.MESSAGE_TYPE.SNACK,
                        "당신은 " + StringUtils.getPostWord(itemName, "을", "를") + " 벗어 가방에 넣었습니다.");
                messageManager.broadcastInRoom(character, String.format("<br>%s %s 벗어 가방에 넣었습니다.",
                        StringUtils.getPostWord(character.getName(), "이", "가"),
                        StringUtils.getPostWord(itemName, "을", "를")));
                break;
            case "갑옷":
                switch (item.getItemtype2()) {
                    case "몸":
                        character.getEquipment().setBody(null);
                        character.addItem(item, 1);
                        objectNode.put(Constants.MESSAGE_TYPE.SNACK,
                                "당신은 " + StringUtils.getPostWord(itemName, "을", "를") + " 벗어 가방에 넣었습니다.");
                        messageManager.broadcastInRoom(character, String.format("<br>%s %s 벗어 가방에 넣었습니다.",
                                StringUtils.getPostWord(character.getName(), "이", "가"),
                                StringUtils.getPostWord(itemName, "을", "를")));
                        break;
                }
                break;
        }

        characterMap.put(name, character);

        return objectNode;
    }

    public int getAttackPower(Character character) {
        logger.debug("getAttackPower " + character.getName() + " " + character.getLevel());
        int attackPower = attackPowerTable[character.getLevel() - 1];

        if (character.getEquipment().getWeapon() != null) {
            attackPower += character.getEquipment().getWeapon().getEffect();
        }

        return attackPower;
    }

    public String recover(String characterName) {
        String retMsg;
        Character character = getCharacter(characterName);

        if (character.getHp() == getMaxHp()) {
            return String.format("당신은 충분히 건강합니다.");
        }

        character.setHp(getMaxHp());

        int recoverMoney = character.getLevel() * 10;
        if (character.getMoney() >= recoverMoney) {
            character.setMoney(character.getMoney() - recoverMoney);
            character.setHp(getMaxHp());
            characterMap.put(characterName, character);
            return String.format("의사가 당신을 회복시켜주었습니다. %d링을 지불했습니다.", recoverMoney);
        } else {
            character.setHp(10);
            characterMap.put(characterName, character);
            return "당신은 회복에 필요한 돈을 가지고 있지 않습니다. 긴급한 치료만을 받았습니다.";
        }
    }

    public String getBody(Character character) {
        int hp = 0;

        if (character.getHp() > 0) {
            hp = character.getHp();
        } else {
            hp = 0;
        }

        return hp + "/" + getMaxHp();
    }

    public int getMoney(Character character) {

        return character.getMoney();
    }

    public Integer getQuestTodo(Character character, Room room) {
        System.out.println("getQuestTodo: " + character.getName() + " " + room.getQuest());

        if (room.getQuest() == null) {
            return null;
        }

        return room.getQuest();
    }

    public String getLook(String characterName, String target) {
        Character character = getCharacter(characterName);
        List<Monster> monsterList = RoomManager.getRoom(character.getRoomId()).getMonsterList();

        for (Monster monster : monsterList) {
            if (monster.getName().equalsIgnoreCase(target)) {

                if (monster.getHp() / monster.getMaxHp() * 100 > 80) {
                    return String.format("%s 아주 건강한 상태입니다.", StringUtils.getPostWord(target, "은", "는"));
                } else if (monster.getHp() / monster.getMaxHp() * 100 > 60) {
                    return String.format("%s 여기저기 생채기가 나있습니다.", StringUtils.getPostWord(target, "은", "는"));
                } else if (monster.getHp() / monster.getMaxHp() * 100 > 40) {
                    return String.format("%s 꽤 많이 다친 것 같습니다.", StringUtils.getPostWord(target, "은", "는"));
                } else if (monster.getHp() / monster.getMaxHp() * 100 > 20) {
                    return String.format("%s 상태가 많이 안좋아 보입니다.", StringUtils.getPostWord(target, "은", "는"));
                } else if (monster.getHp() / monster.getMaxHp() * 100 > 0) {
                    return String.format("%s 죽기 직전입니다..", StringUtils.getPostWord(target, "은", "는"));
                }

            }
        }

        return null;
    }

    public String getCompare(String characterName, String target) {
        Character character = getCharacter(characterName);
        List<Monster> monsterList = RoomManager.getRoom(character.getRoomId()).getMonsterList();

        for (Monster monster : monsterList) {
            if (monster.getName().equalsIgnoreCase(target)) {

                if (character.getLevel() > monster.getLevel() + 3) {
                    return String.format("%s 아주 쉬운 상대입니다.", StringUtils.getPostWord(target, "은", "는"));
                } else if (character.getLevel() > monster.getLevel()) {
                    return String.format("%s 해볼만한 상대입니다.", StringUtils.getPostWord(target, "은", "는"));
                } else if (character.getLevel() == monster.getLevel()) {
                    return String.format("%s 당신과 호적수 입니다.", StringUtils.getPostWord(target, "은", "는"));
                } else if (character.getLevel() + 3 > monster.getLevel()) {
                    return String.format("%s 만만치 않아 보입니다.", StringUtils.getPostWord(target, "은", "는"));
                } else {
                    return String.format("미쳤어요???");
                }
            }
        }

        return null;
    }

    public int move(Character character, String direction) {
        if (character.getRoomId() == 0) {
            character.setRoomId(Integer.parseInt(configManager.getProperty("game.start")));
        }
        Room room = roomManager.getRoom(character.getRoomId());

        switch (direction) {
            case "동":
                if (room.getEast() != 0) {
                    character.setRoomId(room.getEast());
                } else {
                    return -1;
                }
                break;
            case "서":
                if (room.getWest() != 0) {
                    character.setRoomId(room.getWest());
                } else {
                    return -1;
                }
                break;
            case "남":
                if (room.getSouth() != 0) {
                    character.setRoomId(room.getSouth());
                } else {
                    return -1;
                }
                break;
            case "북":
                if (room.getNorth() != 0) {
                    character.setRoomId(room.getNorth());
                } else {
                    return -1;
                }
                break;
            case "위":
                if (room.getUp() != 0) {
                    character.setRoomId(room.getUp());
                } else {
                    return -1;
                }
                break;
            case "아래":
                if (room.getDown() != 0) {
                    character.setRoomId(room.getDown());
                } else {
                    return -1;
                }
                break;
        }

        characterMap.put(character.getName(), character);

        return character.getRoomId();
    }

    public int move(Character character, int roomId) {
        character.setRoomId(roomId);

        return character.getRoomId();
    }
}
