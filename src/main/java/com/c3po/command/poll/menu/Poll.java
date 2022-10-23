package com.c3po.command.poll.menu;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Poll {
    private String question;
    private LocalDateTime dueDate;
    private Long guildId;
    private Long authorId;
    private Long messageId;
    private Long channelId;
    private Long resultChannelId;
    private LocalDateTime createdAt;
    private boolean ended;
    private boolean anonymous;
    private Integer maxVotesPerUser;
    private PollType type;
    private Long roleIdNeededToVote;
    private Integer votePercentageToPass;
    private Long mentionRole;
    private boolean pin;
    private boolean deleteAfterResults;
}