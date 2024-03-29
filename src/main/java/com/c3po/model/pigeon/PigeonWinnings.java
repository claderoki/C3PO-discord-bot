package com.c3po.model.pigeon;

import com.c3po.model.Winnings;
import com.c3po.model.pigeon.stat.core.Stat;
import com.c3po.model.pigeon.stat.StatFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.stream.Collectors;

@Getter
@Setter
public class PigeonWinnings extends Winnings {
    private Double goldModifier;

    @Override
    public String format() {
        return stats
            .values()
            .stream()
            .filter(c -> c.getValue() != 0)
            .map(c -> c.getEmoji() + (c.getValue() > 0 ? "+" : "") + c.getValue())
            .collect(Collectors.joining(", "))
                + (goldModifier == null ? "" : ", \uD83D\uDCAA " +(goldModifier > 0 ? "+" : "") + goldModifier)
        ;
    }

    public void add(PigeonWinnings winnings) {
        addItemIds(winnings.itemIds);
        for (Stat stat: winnings.getStats().values()) {
            stats.computeIfAbsent(stat.getStatType(), c -> StatFactory.create(stat.getStatType(), 0L)).addValue(stat.getValue());
        }

        if (winnings.goldModifier != null) {
            goldModifier += winnings.getGoldModifier();
        }
    }

    /**
     * @param winnings winnings
     * @return A new PigeonWinnings object.
     */
    public static PigeonWinnings merge(PigeonWinnings... winnings) {
        PigeonWinnings newWinnings = new PigeonWinnings();
        for(PigeonWinnings winning: winnings) {
            newWinnings.add(winning);
        }
        return newWinnings;
    }


}
