package mud.api.manager.model;

import mud.api.entity.Character;

public class Message {
    private Character character;
    private String type;
    private String message;

    public Message(Character character, String type, String message) {
        this.character = character;
        this.type = type;
        this.message = message;
    }

    public Character getCharacter() {
        return character;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
