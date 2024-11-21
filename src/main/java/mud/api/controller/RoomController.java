package mud.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mud.api.common.Constants;
import mud.api.entity.Room;
import mud.api.entity.RoomRepository;
import mud.api.manager.RoomManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoomController {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomManager roomManager;

    @GetMapping("/v1/rooms")
    public JsonNode getAllRooms() {
        List<Room> roomList = roomRepository.findAll();

        JsonNode jsonNode = Constants.MAPPER.valueToTree(roomList);
        return jsonNode;
    }

    @GetMapping("/v1/room")
    public JsonNode getRoom(@RequestParam("i") int roomId) {
        Room room = roomManager.getRoom(roomId);
        ObjectNode node = Constants.MAPPER.createObjectNode();
        node.set("room", Constants.MAPPER.valueToTree(room));
        node.put("object_message", roomManager.getObjectMessage(roomId));
        node.put("action_message", roomManager.getActionsMessage(roomId));
        node.put("available_direction", roomManager.getAvailableDirections(roomId));

        return node;
    }
}
