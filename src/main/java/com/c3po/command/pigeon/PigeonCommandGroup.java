package com.c3po.command.pigeon;

import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.CommandGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PigeonCommandGroup extends CommandGroup {
    @Autowired
    public PigeonCommandGroup(
            PigeonProfileCommand profile,
            PigeonExploreCommand explore,
            PigeonSpaceCommand space,
            PigeonCleanCommand clean,
            PigeonPlayCommand play,
            PigeonFeedCommand feed,
            PigeonHealCommand heal,
            PigeonTrainCommand train,
            PigeonBuyCommand buy,
            PigeonPoopCommand poop) {
        super(CommandCategory.PIGEON,"Pigeons");
        addCommands(profile,explore,space,clean,play,feed,heal,train,buy,poop);
    }
}
