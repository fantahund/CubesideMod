package de.fanta.cubeside.util;

import de.iani.cubesideutils.Pair;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.text.Text;

import java.util.List;

public class ItemUtils {
    public static Pair<Integer, Integer> getDamageValuesFormCustomItem(LoreComponent loreComponent) {
        int durablility = -1;
        int maxDurablility = -1;
        List<Text> lines = loreComponent.lines();
        if (!lines.isEmpty()) {
            Text mutableText = lines.getLast();
            if (mutableText != null) {
                String fullDurabilityString = mutableText.getString();
                if (fullDurabilityString.startsWith("Haltbarkeit:") || fullDurabilityString.startsWith("Durability")) {
                    String[] splitFull = fullDurabilityString.split(" ", 2);
                    if (splitFull.length == 2) {
                        String durabilityString = Text.literal(splitFull[1]).getString();
                        String[] splitDurability = durabilityString.split("/", 2);
                        if (splitDurability.length == 2) {
                            try {
                                durablility = Integer.parseInt(splitDurability[0]);
                                maxDurablility = Integer.parseInt(splitDurability[1]);
                            } catch (NumberFormatException ignore) {
                            }
                        }
                    }
                }
            }
        }
        return new Pair<>(durablility, maxDurablility);
    }
}
