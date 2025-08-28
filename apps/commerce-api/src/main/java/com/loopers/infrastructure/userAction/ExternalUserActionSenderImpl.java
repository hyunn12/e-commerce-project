package com.loopers.infrastructure.userAction;

import com.loopers.application.userAction.ExternalUserActionSender;
import com.loopers.domain.event.dto.UserActionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExternalUserActionSenderImpl implements ExternalUserActionSender {

    @Override
    public void send(UserActionEvent event) {
        log.info("외부 시스템 유저 액션 전송: userId={}, action={}, targetId={}", event.getUserId(), event.getAction(), event.getTargetId());
    }
}
