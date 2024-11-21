package mud.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import mud.api.common.Constants;
import mud.api.entity.Monster;
import mud.api.entity.MonsterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MonsterController {

    private int[] maxhpTable = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private int[] attackPowerTable = {58, 10, 16, 23, 28, 34, 40, 46, 51, 58};
    private int[] defencePowerTable = {7, 9, 12, 17, 23, 30, 37, 48, 58, 70};


    @Autowired
    MonsterRepository monsterRepository;

    @GetMapping("/v1/monsters")
    public JsonNode getAllMonsterList() {
        List<Monster> monsterList = monsterRepository.findAll();
        for(Monster monster: monsterList) {

        }

        JsonNode jsonNode = Constants.MAPPER.valueToTree(monsterList);
        return jsonNode;
    }
}
