package mud.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import mud.api.common.Constants;
import mud.api.entity.Account;
import mud.api.entity.Character;
import mud.api.manager.AccountManager;
import mud.api.manager.CharacterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CharacterController {
    private static final Logger logger = LoggerFactory.getLogger(CharacterController.class);

    @Autowired
    AccountManager accountManager;

    @Autowired
    CharacterManager characterManager;

    // GET ALL CHARACTERS
    @GetMapping("/v1/characters")
    public JsonNode getAllCharacters() {
        List<Character> characterList = characterManager.getAllCharacters();

        return Constants.MAPPER.valueToTree(characterList);
    }

    // GET CHARACTERS by UserID
    @GetMapping("/v1/characters/{userid}")
    public JsonNode getCharacters(@PathVariable(name = "userid") String userid) {
        logger.info("userid : " + userid);
        List<Character> characterList = characterManager.getCharacters(userid);

        return Constants.MAPPER.valueToTree(characterList);
    }

    // GET CHARACTER INFO
    @GetMapping("/v1/character")
    public JsonNode getCharacter(@RequestParam("n") String name) {
        Character character = characterManager.getCharacter(name);

        return Constants.MAPPER.valueToTree(character);
    }

    // CREATE CHARACTER
    @PutMapping("/v1/character")
    public ResponseEntity createCharacter(@RequestParam("n") String name, @RequestParam("j") String job, @RequestParam("u") String userId) {
        Account account = accountManager.getAccount(userId);
        Character character = characterManager.createCharacter(name, job, userId);

        if (character == null) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    // SET CHARACTER JOB
    @PutMapping("/v1/character/job")
    public JsonNode updateCharacterJob(@RequestParam("n") String name, @RequestParam("j") String job) {
        Character character = characterManager.setCharacterJob(name, job);

        return Constants.MAPPER.valueToTree(character);
    }
}
