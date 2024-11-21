package mud.api.entity;

public class Equipment {
    private Item head;
    private Item weapon;
    private Item shield;
    private Item shoulder;
    private Item body;
    private Item pants;
    private Item boots;

    public Item getHead() {
        return head;
    }

    public void setHead(Item head) {
        this.head = head;
    }

    public Item getWeapon() {
        return weapon;
    }

    public void setWeapon(Item weapon) {
        this.weapon = weapon;
    }

    public Item getShield() {
        return shield;
    }

    public void setShield(Item shield) {
        this.shield = shield;
    }

    public Item getShoulder() {
        return shoulder;
    }

    public void setShoulder(Item shoulder) {
        this.shoulder = shoulder;
    }

    public Item getBody() {
        return body;
    }

    public void setBody(Item body) {
        this.body = body;
    }

    public Item getPants() {
        return pants;
    }

    public void setPants(Item pants) {
        this.pants = pants;
    }

    public Item getBoots() {
        return boots;
    }

    public void setBoots(Item boots) {
        this.boots = boots;
    }
}
