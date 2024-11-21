package mud.api.manager;

import mud.api.entity.Monster;
import mud.api.entity.MonsterRepository;
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
public class MonsterManager {
    private static final Logger logger = LoggerFactory.getLogger(MonsterManager.class);

    @Autowired
    MonsterRepository monsterRepository;

    //set variables
    private Map<Integer, Monster> monsterMap = new HashMap<>();

    @PostConstruct
    public void run() {

        initializeMonster();
    }

    private void initializeMonster() {
        List<Monster> monsterList = monsterRepository.findAll();
        for(Monster monster : monsterList) {
            monsterMap.put(monster.getId(), monster);
        }

        logger.info("MonsterManager Initialized. Load: " +  monsterMap.size());
    }

    public Monster getMonster(int id) {
        return new Monster(monsterMap.get(id));
    }
}
