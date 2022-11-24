package com.c3po.activitytracker;

import discord4j.core.object.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class InactiveMember {
    private final Member member;
    private final LocalDateTime lastActive;
}
