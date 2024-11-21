package mud.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mud.api.common.ConfigManager;
import mud.api.common.Constants;
import mud.api.common.StringUtils;
import mud.api.entity.Character;
import mud.api.entity.Room;
import mud.api.manager.BattleManager;
import mud.api.manager.CharacterManager;
import mud.api.manager.MessageManager;
import mud.api.manager.RoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class CommandController {
    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    CharacterManager characterManager;

    @Autowired
    RoomManager roomManager;

    @Autowired
    BattleManager battleManager;

    @Autowired
    ConfigManager configManager;

    @Autowired
    MessageManager messageManager;

    private List<String> attackList = Arrays.asList(Constants.ATTACK_LIST);
    private List<String> buyList = Arrays.asList(Constants.BUY_LIST);
    private List<String> compareList = Arrays.asList(Constants.COMPARE_LIST);
    private List<String> directionList = Arrays.asList(Constants.DIRECTION_LIST);
    private List<String> statusList = Arrays.asList(Constants.STATUS_LIST);
    private List<String> inventoryList = Arrays.asList(Constants.INVENTORY_LIST);
    private List<String> lookList = Arrays.asList(Constants.LOOK_LIST);
    private List<String> skillList = Arrays.asList(Constants.SKILL_LIST);
    private List<String> pickUpList = Arrays.asList(Constants.PICKUP_LIST);
    private List<String> sellingList = Arrays.asList(Constants.SELLING_LIST);
    private List<String> wearList = Arrays.asList(Constants.WEAR_LIST);
    private List<String> releaseList = Arrays.asList(Constants.RELEASE_LIST);
    private List<String> eatList = Arrays.asList(Constants.EAT_LIST);
    private List<String> recoverList = Arrays.asList(Constants.RECOVER_LIST);

    @PostMapping("/v1/command")
    public JsonNode doCommand(@RequestParam("n") String characterName, @RequestParam("c") String command) {
        ObjectNode resultNode = Constants.MAPPER.createObjectNode();
        Character character = characterManager.getCharacter(characterName);

        System.out.println(command);
        System.out.println(String.format("n:%s, c:%s", characterName, command));

//        if (command.startsWith("quest>>")) {
//            String[] questArr = command.split(">>");
//            character.updateQuestProceed(questArr[1]);
//            playerManager.move(nickName, player.getRoomId());
//            return setRoom(player, player.getRoomId());
//        }

        String[] commandArr = command.split(" ");
        if (commandArr.length == 1) {
            System.out.println("contains: " + directionList.contains(command));
            if (Constants.CONNECT.equals(command)) {        // 접속
                return Constants.MAPPER.valueToTree(move(character, command));
            } else if (Constants.DIE.equals(command)) {     // 사망
                return Constants.MAPPER.valueToTree(move(character, command));
            } else if (directionList.contains(command)) {   // 이동
                return Constants.MAPPER.valueToTree(move(character, command));
            } else if (inventoryList.contains(command)) {
                resultNode.put(Constants.MESSAGE_TYPE.INVENTORY, characterManager.getInventory(characterName));
                resultNode.put(Constants.MESSAGE_TYPE.INVENTORY_COMMAND, characterManager.getInventoryCommand(characterName));
            } else if (statusList.contains(command)) {
                resultNode.put(Constants.MESSAGE_TYPE.STATUS, characterManager.getStatus(characterName));
                resultNode.put(Constants.MESSAGE_TYPE.STATUS2, characterManager.getEquipmentStatus(characterName));
                resultNode.put(Constants.MESSAGE_TYPE.STATUS_COMMAND, characterManager.getEquipmentReleaseCommand(characterName));
            } else if (skillList.contains(command)) {
//                resultNode.put(Constants.MESSAGE_TYPE.SKILL, playerManager.get)
            } else if (recoverList.contains(command)) {
                resultNode.put(Constants.MESSAGE_TYPE.RECOVER, characterManager.recover(characterName));
                resultNode.put(Constants.MESSAGE_TYPE.BODY, characterManager.getBody(character));
                resultNode.put(Constants.MESSAGE_TYPE.MONEY, characterManager.getMoney(character));
            }
        } else {
            String target = "";
            for (int i = 0; i < commandArr.length - 1; i++) {
                target += commandArr[i];
            }
            String action = commandArr[commandArr.length - 1];

            if (pickUpList.contains(action)) {
                resultNode.put(Constants.MESSAGE_TYPE.PICKUP, characterManager.pickUpItem(characterName, target));
                resultNode.put(Constants.MESSAGE_TYPE.ACTION_RESET, roomManager.getActionsMessage(characterManager.getRoomId(characterName)));
            } else if (sellingList.contains(action)) {
                resultNode.put(Constants.MESSAGE_TYPE.SHOP, characterManager.getShopList(characterName));
                resultNode.put(Constants.MESSAGE_TYPE.SHOP_COMMAND, characterManager.getShopCommandList(characterName));
            } else if (buyList.contains(action)) {
                resultNode.set(Constants.MESSAGE_TYPE.BUY, characterManager.buyItem(characterName, target));
                resultNode.put(Constants.MESSAGE_TYPE.MONEY, characterManager.getMoney(character));
            } else if (eatList.contains(action)) {
                resultNode.put(Constants.MESSAGE_TYPE.EAT, characterManager.eatItem(characterName, target));
            } else if (lookList.contains(action)) {
                resultNode.put(Constants.MESSAGE_TYPE.LOOK, characterManager.getLook(characterName, target));
            } else if (wearList.contains(action)) {
                resultNode.set(Constants.MESSAGE_TYPE.WEAR, characterManager.wearItem(characterName, target));
            } else if (releaseList.contains(action)) {
                resultNode.set(Constants.MESSAGE_TYPE.CONTENT, characterManager.releaseItem(characterName, target));
            } else if (compareList.contains(action)) {
                resultNode.put(Constants.MESSAGE_TYPE.COMPARE, characterManager.getCompare(characterName, target));
            } else if (attackList.contains(action)) {
                resultNode.put(Constants.MESSAGE_TYPE.BATTLE_NEW, battleManager.newBattle(characterName, target));
            }
        }
        return Constants.MAPPER.valueToTree(resultNode);
    }

    public JsonNode move(Character character, String moveTo) {
        ObjectNode node = Constants.MAPPER.createObjectNode();

        // if isBattle, player cannot move
        if (battleManager.isBattle(character.getName())) {
            node.put(Constants.MESSAGE_TYPE.MOVE, false);
            node.put(Constants.MESSAGE_TYPE.MOVE_FAIL, "전투 중에는 이동할 수 없습니다.");

            return node;
        }

        int oldRoomId = character.getRoomId();
        int newRoomId;
        if (Constants.CONNECT.equals(moveTo)) {
            newRoomId = characterManager.move(character, 1);
        } else if (Constants.DIE.equals(moveTo)) {
            newRoomId = characterManager.move(character, 17);
        } else {
            newRoomId = characterManager.move(character, moveTo);
        }

        if (newRoomId == -1) {
            node.put(Constants.MESSAGE_TYPE.MOVE, false);
            node.put(Constants.MESSAGE_TYPE.MOVE_FAIL, "그 방향으로는 이동할 수 없습니다.");

            return node;
        }

        if (!Constants.CONNECT.equals(moveTo) && !Constants.DIE.equals(moveTo)) {
            // broadcast: oldRoom
            messageManager.broadcastInRoom(oldRoomId, "<br> " + StringUtils.getPostWord(character.getName(), "이", "가") +
                    " " + StringUtils.getPostWord(roomManager.getRoom(newRoomId).getRoomname(), "으로", "로") + " 이동했습니다.");
        }

        // broadcast: newRoom
        messageManager.broadcastInRoom(character, String.format("<br> %s 도착했습니다.",
                StringUtils.getPostWord(character.getName(), "이", "가")));
//            newRoomId = Integer.parseInt(configManager.getProperty("game.start"));
        return setRoom(character, newRoomId);
    }

    public JsonNode setRoom(Character character, int roomId) {
        ObjectNode node = Constants.MAPPER.createObjectNode();

        // move
        character.setRoomId(roomId);

        Room room = roomManager.getRoom(roomId);

        // if quests exists, show quest
//        Integer qId = characterManager.getQuestTodo(character, room);
//        String qTodo = null;
//        if (qId != null) {
//            boolean found = false;
//            Iterator<String> iterator = character.getQuestList().iterator();
//            while (iterator.hasNext()) {
//                String quest = iterator.next();
//                if (qId == Integer.parseInt(quest.split("-")[0])) {
//                    found = true;
//
//                    if (questManager.getQuest(quest) != null) {
//                        qTodo = quest;
//                    }
//
//                    break;
//                }
//            }
//
//            if (!found) {
//                player.updateQuestProceed(qId + "-2");
//                qTodo = qId + "-1";
//            }
//        }

        node.put(Constants.MESSAGE_TYPE.ACTION_RESET, true);

//        if (room.getQuest() != null && qTodo != null) {
//            node.put("quest_message", questManager.getQuest(qTodo).getDescription());
//            if (questManager.getQuest(qTodo).getActionList().size() > 0) {
//                node.put("action_message", questManager.getActionMessage(qTodo));
//            }
//
//            return node;
//        }

        node.set("room", Constants.MAPPER.valueToTree(room));
        node.put(Constants.MESSAGE_TYPE.MOVE, true);
        node.put("object_message", roomManager.getObjectMessage(roomId));
        node.put("character_message", messageManager.getCharacterLookInRoom(character));
        node.put("action_message", roomManager.getActionsMessage(roomId));
        node.put("available_direction", roomManager.getAvailableDirections(roomId));
        node.put(Constants.MESSAGE_TYPE.BODY, characterManager.getBody(character));
        node.put(Constants.MESSAGE_TYPE.MONEY, characterManager.getMoney(character));

        logger.info(String.valueOf(Constants.MAPPER.valueToTree(node)));
        return node;
    }
}
