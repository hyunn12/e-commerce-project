package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserActionEvent {

    private Long userId;
    private UserAction action;
    private Long targetId;
    private ZonedDateTime timestamp;

    public static UserActionEvent of(Long userId, UserAction action, Long targetId) {
        return new UserActionEvent(userId, action, targetId, ZonedDateTime.now());
    }
}
