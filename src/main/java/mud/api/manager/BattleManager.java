package mud.api.manager;

import mud.api.common.Constants;
import mud.api.common.StringUtils;
import mud.api.entity.Character;
import mud.api.entity.Monster;
import mud.api.manager.model.BattleMessage;
import mud.api.manager.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BattleManager {
    private static final Logger logger = LoggerFactory.getLogger(BattleManager.class);

    @Autowired
    CharacterManager characterManager;

    @Autowired
    RoomManager roomManager;

    @Autowired
    MessageManager messageManager;

    // set variable
    private int battleValue = 1;

    private Map<Character, Monster> characterBattleMap = new HashMap<>();
    private Map<Monster, Character> monsterBattleMap = new HashMap<>();

    @PostConstruct
    @Scheduled(cron = "*/2 * * * * *")
    public void run() {
        // Character hit Monster
        Iterator pIterator = characterBattleMap.entrySet().iterator();
        while (pIterator.hasNext()) {

            Map.Entry entry = (Map.Entry) pIterator.next();
            Character character = (Character) entry.getKey();
            Monster monster = (Monster) entry.getValue();

            if (character.getHp() > 0 && monster.getHp() > 0) {
                int characterDamage = getCharacterDamage(character, monster);
                BattleMessage battleMessage = getCharacterBattleMessage(character, monster, characterDamage);
                messageManager.put(new Message(character, Constants.MESSAGE_TYPE.BATTLE_P2M, battleMessage.getBattleMessage()));
                messageManager.broadcastInRoom(character, battleMessage.getBroadcastMessage());

                monster.setHp(monster.getHp() - characterDamage);
            }
        }

        // Monster hit Character
        Iterator mIterator = monsterBattleMap.entrySet().iterator();
        while (mIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) mIterator.next();
            Monster monster = (Monster) entry.getKey();
            Character character = (Character) entry.getValue();

            if (character.getHp() > 0 && monster.getHp() > 0) {
                int monsterDamage = getMonsterDamage(character, monster);
                BattleMessage battleMessage = getMonsterBattleMessage(monster, character, monsterDamage);
                messageManager.put(new Message(character, Constants.MESSAGE_TYPE.BATTLE_M2P, battleMessage.getBattleMessage()));
                messageManager.broadcastInRoom(character, battleMessage.getBroadcastMessage());

                character.setHp(character.getHp() - monsterDamage);
                messageManager.put(new Message(character, Constants.MESSAGE_TYPE.BODY, characterManager.getBody(character)));
            }
        }

        // Check Battle Finished
        Iterator p2Iterator = characterBattleMap.entrySet().iterator();
        while (p2Iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) p2Iterator.next();
            Character character = (Character) entry.getKey();
            Monster monster = (Monster) entry.getValue();

            // 2 character vs 1 monster -> 1 character die -> monster hit another character
            if (characterBattleMap.containsKey(character) && !monsterBattleMap.containsKey(monster)) {
                monsterBattleMap.put(monster, character);
            }

            // 1 character vs 2 monster -> 1 monster die -> character hit another monster
            if (!characterBattleMap.containsKey(character) && monsterBattleMap.containsKey(monster)) {
                characterBattleMap.put(character, monster);
            }

            logger.info("턴 결과 : " + character.getName() + "(" + character.getHp() + ") vs " + monster.getName() + "(" + monster.getHp() + ")");
            if (character.getHp() <= 0 || monster.getHp() <= 0) {
                if (character.getHp() <= 0) {
                    messageManager.put(new Message(character, Constants.MESSAGE_TYPE.BATTLE_C_DIE,
                            "당신은 " + monster.getName() + "에게 죽었습니다. 약간의 경험치를 잃었습니다."));
                    messageManager.put(new Message(character, Constants.MESSAGE_TYPE.SNACK,
                            "당신은 " + monster.getName() + "에게 죽었습니다."));
                    messageManager.put(new Message(character, Constants.MESSAGE_TYPE.COMMERCIAL, "die"));

                    messageManager.broadcastInRoom(character, String.format("%s 죽었습니다.",
                            StringUtils.getPostWord(character.getName(), "이", "가")));
                } else if (monster.getHp() <= 0) {
                    characterManager.addExp(character.getName(), monster.getExp());
                    characterManager.addMoney(character.getName(), monster.getMoney());

                    messageManager.put(new Message(character, Constants.MESSAGE_TYPE.BATTLE_M_DIE,
                            "<br><br> 불쌍한 " + monster.getName() + "! 당신에게 경험치를 " + monster.getExp() + "만큼 주고 죽었습니다."
                                    + "<br>당신이 링 " + monster.getMoney() + "개를 집었습니다."));
                    messageManager.put(new Message(character, Constants.MESSAGE_TYPE.SNACK,
                            "당신은 " + StringUtils.getPostWord(monster.getName(), "을", "를") + " 해치웠습니다."));
                    messageManager.put(new Message(character, Constants.MESSAGE_TYPE.BATTLE_END,
                            StringUtils.getPostWord(monster.getName(), "과", "와") + "의 전투가 종료되었습니다."));

                    messageManager.put(new Message(character, Constants.MESSAGE_TYPE.MONEY, String.valueOf(character.getMoney())));

                    messageManager.broadcastInRoom(character, String.format("<br>%s 죽었습니다.",
                            StringUtils.getPostWord(monster.getName(), "이", "가")));

                    roomManager.removeMonster(character.getRoomId(), monster);
                }

                characterBattleMap.remove(character, monster);
                monsterBattleMap.remove(monster, character);
            }
        }
    }

    private BattleMessage getCharacterBattleMessage(Character character, Monster monster, int characterDamage) {
        String battleMessage = null;
        String broadcastMessage = null;

        if (character.getEquipment().getWeapon() == null) {
            if (characterDamage == 0) {
                battleMessage = "<br> %s 공격하는데 실패했습니다.";
                broadcastMessage = "<br> %s %s 공격하는데 실패했습니다.";
            } else if (characterDamage < 5) {
                battleMessage = "<br> %s 간지럽게 때렸습니다.";
                broadcastMessage = "<br> %s %s 간지럽게 때렸습니다.";
            } else if (characterDamage < 10) {
                battleMessage = "<br> %s 살짝 때렸습니다.";
                broadcastMessage = "<br> %s %s 살짝 때렸습니다.";
            } else if (characterDamage < 15) {
                battleMessage = "<br> %s 세게 때렸습니다.";
                broadcastMessage = "<br> %s %s 세게 때렸습니다.";
            } else {
                battleMessage = "<br> %s 강하게 때렸습니다.";
                broadcastMessage = "<br> %s %s 강하게 때렸습니다.";
            }
        } else {
            if (characterDamage == 0) {
                battleMessage = "<br> %s %s 공격하는데 실패했습니다.";
                broadcastMessage = "<br> %s %s 공격하는데 실패했습니다.";
            } else if (characterDamage < 5) {
                battleMessage = "<br> %s %s 가볍게 베었습니다.";
                broadcastMessage = "<br> %s %s 가볍게 베었습니다.";
            } else if (characterDamage < 10) {
                battleMessage = "<br> %s %s 살짝 베었습니다.";
                broadcastMessage = "<br> %s %s 살짝 베었습니다.";
            } else if (characterDamage < 15) {
                battleMessage = "<br> %s %s 세게 베었습니다.";
                broadcastMessage = "<br> %s %s 세게 베었습니다.";
            } else {
                battleMessage = "<br> %s %s 강하게 베었습니다.";
                broadcastMessage = "<br> %s %s %s 강하게 베었습니다.";
            }
        }

        String retMsg = null;
        String broadcastMsg = null;
        if (character.getEquipment().getWeapon() == null) {
            retMsg = String.format(battleMessage, StringUtils.getPostWord(monster.getName(), "을", "를"));
            broadcastMsg = String.format(broadcastMessage, StringUtils.getPostWord(character.getName(), "이", "가"),
                    StringUtils.getPostWord(monster.getName(), "을", "를"));
        } else {
            retMsg = String.format(battleMessage, StringUtils.getPostWord(monster.getName(), "을", "를"),
                    StringUtils.getPostWord(character.getEquipment().getWeapon().getItemname(), "으로", "로"));
            broadcastMsg = String.format(battleMessage, StringUtils.getPostWord(character.getName(), "이", "가"),
                    StringUtils.getPostWord(monster.getName(), "을", "를"),
                    StringUtils.getPostWord(character.getEquipment().getWeapon().getItemname(), "으로", "로"));
        }
        return new BattleMessage(retMsg, broadcastMsg);
    }

    private BattleMessage getMonsterBattleMessage(Monster monster, Character character, int monsterDamage) {
        String battleMessage = null;
        String broadcastMessage = null;

        if (monsterDamage == 0) {
            battleMessage = "<br> %s 당신에게 헛손질을 했습니다.";
            broadcastMessage = "<br> %s %s 헛손질을 했습니다.";
        } else if (monsterDamage < 5) {
            battleMessage = "<br> %s 당신을 간지럽게 때렸습니다.";
            broadcastMessage = "<br> %s %s 간지럽게 때렸습니다.";
        } else if (monsterDamage < 10) {
            battleMessage = "<br> %s 당신을 살살 때렸습니다.";
            broadcastMessage = "<br> %s %s 간지럽게 때렸습니다.";
        } else if (monsterDamage < 15) {
            battleMessage = "<br> %s 당신을 세게 때렸습니다.";
            broadcastMessage = "<br> %s %s 세게 때렸습니다.";
        } else {
            battleMessage = "<br> %s 당신을 강하게 때렸습니다.";
            broadcastMessage = "<br> %s %s 강하게 때렸습니다.";
        }

        return new BattleMessage(String.format(battleMessage, StringUtils.getPostWord(monster.getName(), "이", "가")),
                String.format(broadcastMessage, StringUtils.getPostWord(monster.getName(), "이", "가"),
                        StringUtils.getPostWord(character.getName(), "을", "를")));
    }

    private int getCharacterDamage(Character character, Monster monster) {
        int characterAtk = characterManager.getAttackPower(character);
        int monsterDef = monster.getDefencePower();

        if (characterAtk < monsterDef) {
            monsterDef = characterAtk - 1;
        }

        Random random = new Random();
        int retDamage = Math.round((characterAtk - monsterDef) * battleValue * random.nextFloat());

        return retDamage;
    }

    private int getMonsterDamage(Character character, Monster monster) {
        int monsterAtk = monster.getDefencePower();
        int characterDef = characterManager.getDefencePower(character);

        Random random = new Random();
        int retDamage = Math.round((monsterAtk - characterDef) * battleValue * random.nextFloat());

        return retDamage;
    }

    public String newBattle(String characterName, String monsterName) {
        boolean monsterExist = false;
        Monster targetMonster = null;
        Character character = characterManager.getCharacter(characterName);
        List<Monster> monsterList = roomManager.getMonsterList(character.getRoomId());
        for (Monster monster : monsterList) {
            if (monster.getName().equals(monsterName)) {
                monsterExist = true;
                targetMonster = monster;
            }
        }

        if (!monsterExist) {
            return String.format("여기는 %s 없습니다.", StringUtils.getPostWord(monsterName, "이", "가"));
        }

        characterBattleMap.put(character, targetMonster);
        if (!monsterBattleMap.containsKey(targetMonster)) {
            monsterBattleMap.put(targetMonster, character);
        }

        messageManager.broadcastInRoom(character, String.format("<br>%s %s 공격합니다.",
                StringUtils.getPostWord(characterName, "이", "가"),
                StringUtils.getPostWord(monsterName, "을", "를")));

        return String.format("당신이 %s 공격합니다.", StringUtils.getPostWord(monsterName, "을", "를"));
    }

    public boolean isBattle(String characterName) {
        Character character = characterManager.getCharacter(characterName);

        Iterator pIterator = characterBattleMap.entrySet().iterator();
        while (pIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) pIterator.next();
            Character chr = (Character) entry.getKey();

            if (character.getName().equals(chr.getName())) {
                return true;
            }
        }
        return false;
    }


    private void useExample() {
        String name;
        name = "네이버";
        System.out.println(StringUtils.getPostWord(name, "으로", "로"));
        System.out.println(StringUtils.getPostWord(name, "을", "를"));
        System.out.println(StringUtils.getPostWord(name, "이", "가"));
        System.out.println(StringUtils.getPostWord(name, "은", "는"));
        System.out.println(StringUtils.getPostWord(name, "과", "와"));
    }


}
