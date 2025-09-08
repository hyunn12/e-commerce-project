package com.loopers.domain.handled;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventHandledService {

    private final EventHandledRepository eventHandledRepository;

    @Transactional
    public boolean markHandled(String eventId, String group) {
        try {
            eventHandledRepository.save(EventHandled.of(eventId, group));
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
