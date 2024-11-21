package mud.api.manager.model;

public class BattleMessage {
    private String battleMessage;
    private String broadcastMessage;

    public BattleMessage(String battleMessage, String broadcastMessage) {
        this.battleMessage = battleMessage;
        this.broadcastMessage = broadcastMessage;
    }

    public String getBattleMessage() {
        return battleMessage;
    }

    public void setBattleMessage(String battleMessage) {
        this.battleMessage = battleMessage;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public void setBroadcastMessage(String broadcastMessage) {
        this.broadcastMessage = broadcastMessage;
    }
}
