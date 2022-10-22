package com.c3po.command.poll.menu;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class Poll {
    private final String question;
    private final LocalDateTime dueDate;
    private final Long guildId;
    private final Long authorId;
    private final Long messageId;
    private final Long channelId;
    private final Long resultChannelId;
    private final LocalDateTime createdAt;
    private final boolean ended;
    private final boolean anonymous;
    private final boolean maxVotesPerUser;
    private final String type;
    private final Long roleIdNeededToVote;
    private final Integer votePercentageToPass;
    private final Long mentionRole;
    private final boolean pin;
    private final boolean deleteAfterResults;
}