package com.c3po.connection.repository;

import com.c3po.command.insecta.core.Insecta;
import com.c3po.command.insecta.core.InsectaFactory;
import com.c3po.command.insecta.core.InsectaProfile;
import com.c3po.command.insecta.core.Insectarium;
import com.c3po.command.insecta.model.InsectaWinning;
import com.c3po.connection.Repository;
import com.c3po.database.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsectaRepository extends Repository {
    public synchronized void saveWinnings(InsectaWinning winning) {
        String query = """
            INSERT INTO `insecta_winnings` (`key`, `value`, `user_id`, `collected`)
            VALUES (?,?,?,?)
            """;
        execute(query,
            new StringParameter(winning.getKey()),
            new LongParameter(winning.getValue()),
            new LongParameter(winning.getUserId()),
            new BoolParameter(winning.isCollected())
        );
    }

    protected synchronized void updateProfile(InsectaProfile profile) {
        String query = "UPDATE `insecta_profile` SET `hexacoin` = ?, `lastCollected` = ? WHERE `id` = ?";
        execute(query,
            new LongParameter(profile.hexacoin()),
            new DateTimeParameter(profile.lastCollected()),
            new IntParameter(profile.id())
        );
    }

    protected synchronized void createProfile(InsectaProfile profile) {
        String query = "INSERT INTO `insecta_profile` VALUES (`hexacoin`, `lastCollected`) (?, ?)";
        execute(query,
            new LongParameter(profile.hexacoin()),
            new DateTimeParameter(profile.lastCollected())
        );
    }

    public synchronized void saveProfile(InsectaProfile profile) {
        if (profile.id() == null) {
            createProfile(profile);
        } else {
            updateProfile(profile);
        }
    }

    public synchronized Insectarium getInsectarium(int profileId) {
        Map<Insecta, Long> infos = getMany("SELECT `type`, `amount` FROM `insecta_info` WHERE profile_id = ?", new IntParameter(profileId))
            .stream()
            .collect(Collectors.toMap(c -> InsectaFactory.get(c.getString("type")), c -> c.getLong("amount")));
        return new Insectarium(infos);
    }

    public synchronized InsectaProfile getProfile(long userId) {
        var result = getOne("SELECT * FROM `insecta_profile` WHERE user_id = ?", new LongParameter(userId));
        if (result == null) {
            return null;
        }
        int id = result.getInt("id");


//        return getOne("")
        return null;
    }
}