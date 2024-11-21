package mud.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import mud.api.entity.Npc;
import mud.api.entity.NpcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NpcController {
    @Autowired
    NpcRepository npcRepository;

//    @GetMapping("/v1/npc/look")
//    public JsonNode getNpcLook(String ) {
//        List<Npc> npcList = npcRepository.
//    }

}
