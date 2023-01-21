package com.c3po.connection.repository;

import com.c3po.command.insecta.core.Insecta;
import com.c3po.command.insecta.core.InsectaFactory;
import com.c3po.command.insecta.core.InsectaProfile;
import com.c3po.command.insecta.core.Insectarium;
import com.c3po.command.insecta.model.InsectaWinningDTO;
import com.c3po.connection.Repository;
import com.c3po.database.*;
import com.c3po.database.result.Result;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsectaRepository extends Repository {
    public synchronized void saveWinnings(InsectaWinningDTO winning) {
        String query = """
            INSERT INTO `insecta_winning` (`key`, `value`, `user_id`, `collected`, `initial_datetime`)
            VALUES (?,?,?,?, ?)
            """;
        execute(query,
            new StringParameter(winning.getKey()),
            new LongParameter(winning.getValue()),
            new LongParameter(winning.getUserId()),
            new BoolParameter(winning.isCollected()),
            new DateTimeParameter(winning.getInitialDate())
        );
    }

    public synchronized List<InsectaWinningDTO> getUncollectedWinnings(long userId) {
        String query = "SELECT * FROM `insecta_winning` WHERE `collected` = 0 AND `user_id` = ?";
        return getMany(query, new LongParameter(userId))
            .stream()
            .map(c -> InsectaWinningDTO.builder()
                .key(c.getString("key"))
                .value(c.getLong("value"))
                .userId(c.getLong("user_id"))
                .collected(false)
                .initialDate(c.getDateTime("initial_datetime"))
                .build())
            .toList();
    }

    public synchronized void removeAllCollected(long userId) {
        String query = "DELETE FROM `insecta_winning` WHERE `user_id` = ?";
        execute(query, new LongParameter(userId));
    }

    protected synchronized void saveInfo(int profileId, String key, long amount) {
        String query = "INSERT INTO `insecta_info` (`key`, `amount`, `profile_id`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `amount` = ?";
        execute(query,
            new StringParameter(key),
            new LongParameter(amount),
            new IntParameter(profileId),
            new LongParameter(amount)
        );
    }

    protected synchronized void saveInsectarium(int profileId, Insectarium insectarium) {
        for(var entrySet: insectarium.getValues().entrySet()) {
            saveInfo(profileId, entrySet.getKey().getKey(), entrySet.getValue());
        }
    }

    public synchronized void updateProfile(InsectaProfile profile) {
        String query = "UPDATE `insecta_profile` SET `hexacoin` = ?, `last_collected` = ? WHERE `id` = ?";
        execute(query,
            new LongParameter(profile.getHexacoin()),
            new DateTimeParameter(profile.getLastCollected()),
            new IntParameter(profile.getId())
        );
        saveInsectarium(profile.getId(), profile.getInsectarium());
    }

    public synchronized void createProfile(InsectaProfile profile) {
        String query = "INSERT INTO `insecta_profile` (`hexacoin`, `last_collected`, `user_id`) VALUES (?, ?, ?)";
        execute(query,
            new LongParameter(profile.getHexacoin()),
            new DateTimeParameter(profile.getLastCollected()),
            new LongParameter(profile.getUserId())
        );
        saveInsectarium(profile.getId(), profile.getInsectarium());
        profile.setId(getProfileId(profile.getUserId()));
    }

    public synchronized Insectarium getInsectarium(int profileId) {
        Map<Insecta, Long> infos = getMany("SELECT `key`, `amount` FROM `insecta_info` WHERE profile_id = ?", new IntParameter(profileId))
            .stream()
            .collect(Collectors.toMap(c -> InsectaFactory.get(c.getString("key")), c -> c.getLong("amount")));
        return new Insectarium(new HashMap<>(infos));
    }

    private synchronized Integer getProfileId(long userId) {
        String query = "SELECT `id` FROM `insecta_profile` WHERE `user_id` = ?";
        Result result = getOne(query, new LongParameter(userId));
        if (result == null) {
            return null;
        }
        return result.getInt("id");
    }

    public synchronized InsectaProfile getProfile(long userId) {
        var result = getOne("SELECT * FROM `insecta_profile` WHERE user_id = ?", new LongParameter(userId));
        if (result == null) {
            return null;
        }
        int id = result.getInt("id");
        return InsectaProfile.builder()
            .id(id)
            .hexacoin(result.getLong("hexacoin"))
            .lastCollected(result.getDateTime("last_collected"))
            .userId(result.getLong("user_id"))
            .insectarium(getInsectarium(id))
            .build();
    }
}