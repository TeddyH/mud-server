package mud.api.entity;

import mud.api.manager.ItemManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Npc {
    @Id
    private int id;
    private String npcName;
    private String look;
    private String actionOnRoad;
    private String selling;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public String getLook() {
        return look;
    }

    public void setLook(String look) {
        this.look = look;
    }

    public String getActionOnRoad() {
        return actionOnRoad;
    }

    public void setActionOnRoad(String actionOnRoad) {
        this.actionOnRoad = actionOnRoad;
    }

    public String getSelling() {
        return selling;
    }

    public void setSelling(String selling) {
        this.selling = selling;
    }

}
