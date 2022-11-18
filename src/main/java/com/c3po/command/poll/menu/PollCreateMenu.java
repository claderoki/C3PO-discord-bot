package com.c3po.command.poll.menu;

import com.c3po.core.DataFormatter;
import com.c3po.core.command.Context;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.Emoji;
import com.c3po.helper.waiter.DurationParser;
import com.c3po.helper.waiter.IntParser;
import com.c3po.ui.input.BooleanMenuOption;
import com.c3po.ui.input.VoidMenuOption;
import com.c3po.ui.input.WaiterMenuOption;
import com.c3po.ui.input.base.EnumSelectMenuMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuOption;
import discord4j.core.spec.EmbedCreateSpec;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PollCreateMenu extends Menu {
    private final Poll poll;

    public PollCreateMenu(Context context, Poll poll) {
        super(context);
        this.poll = poll;
        setEmbedConsumer(this::setupEmbedConsumer);
        this.addOptions();
    }

    private Map<String, String> getPollFormat() {
        return Map.ofEntries(
            Map.entry("Pin", DataFormatter.prettify(poll.isPin())),
            Map.entry("Anonymous", DataFormatter.prettify(poll.isAnonymous())),
            Map.entry("Question", DataFormatter.prettify(poll.getQuestion())),
            Map.entry("Channel (ID)", DataFormatter.prettify(poll.getChannelId())),
            Map.entry("Result channel (ID)", DataFormatter.prettify(poll.getResultChannelId())),
            Map.entry("Max votes (per user)", DataFormatter.prettify(poll.getMaxVotesPerUser())),
            Map.entry("type", DataFormatter.prettify(poll.getType())),
            Map.entry("Role needed", DataFormatter.prettify(poll.getRoleIdNeededToVote())),
            Map.entry("Vote % to pass", DataFormatter.prettify(poll.getVotePercentageToPass())),
            Map.entry("Role to mention", DataFormatter.prettify(poll.getMentionRole())),
            Map.entry("Due date", DataFormatter.prettify(poll.getDueDate()))
        );
    }

    private void setupEmbedConsumer(EmbedCreateSpec.Builder builder) {
        Map<String, String> pollFormat = getPollFormat();
        int longestWord = pollFormat.keySet().stream().mapToInt(String::length).max().orElseThrow();
        var values = pollFormat.entrySet().stream().map(c -> c.getKey() + " ".repeat(longestWord-c.getKey().length()) +" => " + c.getValue()).collect(Collectors.joining("\n"));
        builder.description("```\n%s```".formatted(values));
    }

    private boolean validatePoll() {
        return poll.getQuestion() != null;
    }

    /*

    These will be created automatically:
    Long messageId;
    LocalDateTime createdAt;
    Long guildId;
    Long authorId;

    String question;
    LocalDateTime dueDate;
    Long channelId;
    Long resultChannelId;
    boolean maxVotesPerUser;
    PollType type;
    Long roleIdNeededToVote;
    Integer votePercentageToPass;
    Long mentionRole;
    * */

    private List<MenuOption<?,?,?>> getAvailableOptions() {
        return List.of(
                // Mandatory are: Channel, Question, due date.


//            new BooleanMenuOption("Pin?", false)
//                .setSetter(poll::setPin)
//                .setEmoji(Emoji.PIN),
//            new BooleanMenuOption("Delete after results?", false)
//                .setSetter(poll::setDeleteAfterResults)
//                .setEmoji(Emoji.TRASH),
//            new WaiterMenuOption<>("Due date", DurationParser.builder().build())
//                .setSetter(v -> poll.setDueDate(DateTimeHelper.now().plus(v)))
//                .setEmoji(Emoji.CALENDAR),
//            new BooleanMenuOption("Anonymous?", false)
//                .setSetter(poll::setAnonymous)
//                .setEmoji(Emoji.TRASH),
//            new WaiterMenuOption<>("Vote percentage to pass", IntParser.builder().min(0).max(100).build())
//                .setDisabledIf(v -> poll.getType() != null && poll.getType().equals(PollType.BOOL))
//                .setSetter(poll::setVotePercentageToPass)
//                .setEmoji(Emoji.CALENDAR),
//            new EnumSelectMenuMenuOption<>("Vote percentage to pass", PollType.class, v -> v.name().toLowerCase())
//                .setSetter(v -> poll.setType(v.get(0)))
//                .setEmoji(Emoji.CALENDAR),


            new VoidMenuOption("Create")
                .setDisabledIf((e) -> !validatePoll())
                .setEmoji(Emoji.TRASH)
        );
    }

    private void addOptions() {
        getAvailableOptions().forEach(this::addOption);
    }
}
