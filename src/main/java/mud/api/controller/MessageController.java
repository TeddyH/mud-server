package mud.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import mud.api.entity.Character;
import mud.api.manager.CharacterManager;
import mud.api.manager.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @Autowired
    CharacterManager characterManager;

    @Autowired
    MessageManager messageManager;

    @GetMapping("/v1/message")
    public JsonNode getMessage(@RequestParam("n") String name) {
        Character character = characterManager.getCharacter(name);

        return messageManager.getMessage(character);
    }
}
