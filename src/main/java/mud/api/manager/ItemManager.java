package mud.api.manager;

import com.fasterxml.jackson.databind.JsonNode;
import mud.api.common.Constants;
import mud.api.entity.Item;
import mud.api.entity.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ItemManager {
    private static final Logger logger = LoggerFactory.getLogger(ItemManager.class);

    @Autowired
    ItemRepository itemRepository;

    //set variables
    private Map<Integer, Item> itemMap = new HashMap<>();

    @PostConstruct
    public void run() {
        initialize();
    }

    private void initialize() {
        List<Item> itemList = itemRepository.findAll();
        for (Item item : itemList) {
            itemMap.put(item.getId(), item);
        }

        logger.info("ItemManager Initialized. Load: " + itemMap.size());
    }

    public Item getItem(int id) throws NullPointerException {
        if(itemMap.get(id) == null) {
            logger.debug("getItem = null");
        } else {
            logger.debug("getItem " + itemMap.get(id).getItemname());
        }
        return itemMap.get(id);
    }

    public Item getItem(String itemName) {
        for(Map.Entry<Integer, Item> element : itemMap.entrySet()) {
            if(element.getValue().getItemname().equals(itemName)) {
                return element.getValue();
            }
        }

        return null;
    }

    public void loadItems() {
        List<Item> itemList = itemRepository.findAll();

        JsonNode jsonNode = Constants.MAPPER.valueToTree(itemList);
        logger.debug(String.valueOf(jsonNode));
    }
}
