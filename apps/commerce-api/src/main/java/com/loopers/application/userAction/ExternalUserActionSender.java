package com.loopers.application.userAction;

import com.loopers.domain.event.dto.UserActionEvent;

public interface ExternalUserActionSender {

    void send(UserActionEvent event);
}
