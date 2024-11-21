package mud.api.manager;

import mud.api.entity.Item;
import mud.api.entity.Npc;
import mud.api.entity.NpcRepository;
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
public class NpcManager extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(NpcManager.class);

    @Autowired
    NpcRepository npcRepository;

    @Autowired
    ItemManager itemManager;

    // set variables
    private Map<Integer , Npc> npcMap = new HashMap<>();

    @PostConstruct
    public void run() {
        initializeAllNpc();
    }

    private void initializeAllNpc() {
        List<Npc> npcList = npcRepository.findAll();
        for(Npc npc : npcList) {
            npcMap.put(npc.getId(), npc);
        }

        logger.info("NpcManager Initialized. Load: " + npcMap.size());
    }

    public Npc getNpc(int npcId) {
        return npcMap.get(npcId);
    }

    public String getSellingList(Npc npc) {
        String ret = "";

        if (npc.getSelling() != null) {
            String[] items = npc.getSelling().split(",");
            for (String itemId : items) {
                Item item = itemManager.getItem(Integer.parseInt(itemId));
                ret += "<br>- " + item.getItemname() + " : " + item.getPrice() + " 링";
            }
        }

        return ret;
    }

    public String getSellingCommandList(Npc npc) {
        String ret = "";
        if (npc.getSelling() != null) {
            String[] items = npc.getSelling().split(",");
            for (String itemId : items) {
                Item item = itemManager.getItem(Integer.parseInt(itemId));
                ret += item.getItemname() + " 산다&&";
            }
        }

        return ret;
    }

}
