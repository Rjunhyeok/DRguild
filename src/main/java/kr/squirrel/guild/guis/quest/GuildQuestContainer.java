package kr.squirrel.guild.guis.quest;

import kr.squirrel.guild.guis.Container;
import org.bukkit.event.inventory.InventoryEvent;

public class GuildQuestContainer extends Container {

    public GuildQuestContainer() {
        super(9 * 5, "[ 길드 퀘스트 ]");
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {

    }
}
