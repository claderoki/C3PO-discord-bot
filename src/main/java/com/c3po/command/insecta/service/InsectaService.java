package com.c3po.command.insecta.service;

import com.c3po.command.insecta.core.InsectaProfile;
import com.c3po.command.insecta.core.InsectaWinnings;
import com.c3po.helper.DateTimeHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsectaService {
    public InsectaWinnings collect(InsectaProfile profile) {
        LocalDateTime now = DateTimeHelper.now();
        LocalDateTime lastCollected = profile.getLastCollected();
        long seconds = ChronoUnit.SECONDS.between(lastCollected, now);

        return new InsectaWinnings(profile.getInsectarium().getValues()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                c -> c.getKey().getRatePerSecond()*c.getValue()*seconds)
            )
        );
    }
}
