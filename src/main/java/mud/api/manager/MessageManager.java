package mud.api.manager;


import com.fasterxml.jackson.databind.node.ObjectNode;
import mud.api.common.Constants;
import mud.api.common.StringUtils;
import mud.api.entity.Character;
import mud.api.manager.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MessageManager {
    private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);

    private List<Character> characterList = new ArrayList<>();
    private List<Message> messageList = new ArrayList<>();

    @PostConstruct
    public void run() {

    }

    public void put(Message message) {
        messageList.add(message);
    }

    public ObjectNode getMessage(Character character) {
        ObjectNode objectNode = Constants.MAPPER.createObjectNode();
//        Iterator iterator = messageList.iterator();
//        while(iterator.hasNext()) {
//            Message message = (Message) iterator.next();
//            if(player.equals(message.getPlayer())) {
//                if(!objectNode.has(message.getType())) {
//                    objectNode.put(message.getType(), message.getMessage());
//                } else {
//                    String concatMessage = objectNode.get(message.getType()).toString().substring(1,
//                            objectNode.get(message.getType()).toString().length() -1) + message.getMessage();
//                    objectNode.put(message.getType(), concatMessage);
//                }
//                iterator.remove();
//            }
//        }

        for (Iterator iterator = messageList.iterator(); iterator.hasNext(); ) {
            Message message = (Message) iterator.next();
            if (character.equals(message.getCharacter())) {
                if (!objectNode.has(message.getType())) {
                    objectNode.put(message.getType(), message.getMessage());
                } else {
                    String concatMessage = objectNode.get(message.getType()).toString().substring(1,
                            objectNode.get(message.getType()).toString().length() - 1) + message.getMessage();
                    objectNode.put(message.getType(), concatMessage);
                }
                iterator.remove();
            }
        }

        if (objectNode != null && objectNode.size() > 0) {
            logger.debug(character.getName() + " " + objectNode);
        }

        return Constants.MAPPER.valueToTree(objectNode);
    }

    public void broadcast(String message) {
        for (Character character : characterList) {
            put(new Message(character, Constants.MESSAGE_TYPE.BROADCAST, message));
        }
    }

    public void updateCharacterList(List<Character> characterList) {
        this.characterList = characterList;
    }

    public List<Character> characterInSameRoom(Character character) {
        List<Character> resultList = new ArrayList<>();
        for (Character chr : characterList) {
            if (character.getRoomId() == chr.getRoomId() && character.getId() != chr.getId()) {
                resultList.add(chr);
            }
        }

        return resultList;
    }

    public List<Character> characterInRoom(int roomId) {
        List<Character> resultList = new ArrayList<>();
        for (Character chr : characterList) {
            if (roomId == chr.getRoomId()) {
                resultList.add(chr);
            }
        }

        return resultList;
    }

    public void broadcastInRoom(Character character, String message) {
        List<Character> characterList = characterInSameRoom(character);
        for (Character chr : characterList) {
            put(new Message(chr, Constants.MESSAGE_TYPE.BROADCAST, message));
        }
    }

    public void broadcastInRoom(int roomId, String message) {
        List<Character> characterList = characterInRoom(roomId);
        for (Character chr : characterList) {
            put(new Message(chr, Constants.MESSAGE_TYPE.BROADCAST, message));
        }
    }

    public String getCharacterLookInRoom(Character character) {
        String retMsg = "";
        List<Character> characterList = characterInSameRoom(character);
        for (Character chr : characterList) {
            retMsg += String.format("<br>%s 서있습니다.",
                    StringUtils.getPostWord(chr.getName(), "이", "가"));
        }

        return retMsg;
    }
}
