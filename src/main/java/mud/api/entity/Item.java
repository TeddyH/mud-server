package mud.api.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Item {
    @Id
    private int id;
    private String itemname;
    private String description;
    private String look;
    private String itemtype;
    private String itemtype2;
    private String actiononroad;
    private String actionininventory;
    private int price;
    private String effecttype;
    private int effect;

    private Item() {

    }

    public Item(int id, String itemname, String description, String look, String itemtype, String itemtype2,
                String actiononroad, String actionininventory, int price, String effecttype, int effect) {
        this.id = id;
        this.itemname = itemname;
        this.look = look;
        this.itemtype = itemtype;
        this.itemtype2 = itemtype2;
        this.actiononroad = actiononroad;
        this.actionininventory = actionininventory;
        this.price = price;
        this.effecttype = effecttype;
        this.effect = effect;
    }

    public Item(Item item) {
        this(item.getId(), item.getItemname(), item.getDescription(), item.getLook(), item.getItemtype(),
                item.getItemtype2(), item.getActiononroad(), item.getActionininventory(), item.getPrice(),
                item.getEffecttype(), item.getEffect());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLook() {
        return look;
    }

    public void setLook(String look) {
        this.look = look;
    }

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getItemtype2() {
        return itemtype2;
    }

    public void setItemtype2(String itemtype2) {
        this.itemtype2 = itemtype2;
    }

    public String getActiononroad() {
        return actiononroad;
    }

    public void setActiononroad(String actiononroad) {
        this.actiononroad = actiononroad;
    }

    public String getActionininventory() {
        return actionininventory;
    }

    public void setActionininventory(String actionininventory) {
        this.actionininventory = actionininventory;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getEffecttype() {
        return effecttype;
    }

    public void setEffecttype(String effecttype) {
        this.effecttype = effecttype;
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }
}
