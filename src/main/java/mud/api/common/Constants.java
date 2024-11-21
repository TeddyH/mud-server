package mud.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String TYPE_MOVE = "이동";
    public static final String CONNECT = "접속";
    public static final String DIE = "사망";
    public static final String[] COMPARE_LIST = new String[]{"비교", "비교한다"};
    public static final String[] ATTACK_LIST = new String[]{"친다", "쳐", "때려", "공격", "공격한다"};
    public static final String[] BUY_LIST = new String[]{"산다", "사", "구매"};
    public static final String[] DIRECTION_LIST = new String[]{"동", "서", "남", "북", "위", "아래"};
    public static final String[] EAT_LIST = new String[]{"마셔", "먹어"};
    public static final String[] INVENTORY_LIST = new String[]{"가방", "인벤토리", "인벤"};
    public static final String[] LOOK_LIST = new String[]{"본다", "보다", "봐"};
    public static final String[] PICKUP_LIST = new String[]{"주워", "줍다", "집다", "집는다"};
    public static final String[] RECOVER_LIST = new String[]{"회복", "회복하기"};
    public static final String[] RELEASE_LIST = new String[]{"벗어", "해제"};
    public static final String[] SELLING_LIST = new String[]{"판매 목록", "목록"};
    public static final String[] SKILL_LIST = new String[]{"능력", "스킬"};
    public static final String[] STATUS_LIST = new String[]{"상태", "상황"};
    public static final String[] WEAR_LIST = new String[]{"쥐어", "입어"};
//    public static final String QUERY_ITEM_LIST = "SELECT * FROM mud.item";
//    public static final String QUERY_NPC_LIST = "SELECT * FROM mud.npc";
//    public static final String QUERY_ROOM_LIST = "SELECT * FROM mud.room";
//    public static final String QUERY_QUEST_LIST = "SELECT * FROM mud.quest";
//    public static final String QUERY_PLAYER_LIST = "SELECT * FROM mud.player";

    public class MESSAGE_TYPE {
        public static final String ACTION = "action";
        public static final String ACTION_RESET = "action_reset";
        public static final String BATTLE = "battle";
        public static final String BATTLE_NEW = "battle_new";
        public static final String BATTLE_P2M = "battle_p2m";
        public static final String BATTLE_M2P = "battle_m2p";
        public static final String BATTLE_C_DIE = "battle_character_die";
        public static final String BATTLE_M_DIE = "battle_monster_die";
        public static final String BATTLE_END = "battle_end";
        public static final String BODY = "body";
        public static final String BROADCAST = "broadcast";
        public static final String BUY = "buy";
        public static final String COMMERCIAL = "commercial";
        public static final String COMPARE = "compare";
        public static final String CONTENT = "content";
        public static final String EAT = "eat";
        public static final String INVENTORY = "inventory";
        public static final String INVENTORY_COMMAND = "inventory_command";
        public static final String LEVEL_UP = "level_up";
        public static final String LOOK = "look";
        public static final String MONEY = "money";
        public static final String MOVE = "move";
        public static final String MOVE_FAIL = "move_fail";
        public static final String PICKUP = "pickup";
        public static final String RECOVER = "recover";
        public static final String SHOP = "shop";
        public static final String SHOP_COMMAND = "shop_command";
        public static final String SKILL = "skill";
        public static final String STATUS = "status";
        public static final String STATUS2 = "status2";
        public static final String STATUS_COMMAND = "status_command";
        public static final String TITLE = "title";
        public static final String TITLE2 = "title2";
        public static final String SNACK = "snack";
        public static final String WEAR = "wear";
    }
}
