package mud.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import mud.api.common.Constants;
import mud.api.entity.Item;
import mud.api.entity.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemController {

    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/v1/items")
    public JsonNode getAllItemList() {
        List<Item> itemList = itemRepository.findAll();

        JsonNode jsonNode = Constants.MAPPER.valueToTree(itemList);
        return jsonNode;
    }
}
