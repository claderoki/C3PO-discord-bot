package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.connection.query.QueryBuilder;
import com.c3po.core.ScopeTarget;
import com.c3po.database.*;
import com.c3po.helper.EncryptionHelper;
import com.c3po.model.credential.Credential;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CredentialRepository extends Repository {
    private void addTargetToQuery(QueryBuilder query, ScopeTarget target) {
        if (target.getGuildId() != null) {
            query.addWhere(" AND `guild_id` = ? ", new LongParameter(target.getGuildId()));
        }
        if (target.getUserId() != null) {
            query.addWhere(" AND `user_id` = ? ", new LongParameter(target.getUserId()));
        }
    }

    private Credential toModel(Result result) {
        return new Credential(
            result.getInt("id"),
            result.getString("category"),
            result.getString("key"),
            result.getDecryptedString("value"),
            ScopeTarget.of(result.optLong("user_id"), result.optLong("guild_id"))
        );
    }

    public Flux<Credential> getCredentials(String category, ScopeTarget target) {
        QueryBuilder query = new QueryBuilder("SELECT `id`, `key`, `value` FROM `credential`");
        addTargetToQuery(query, target);
        query.addWhere("`category` = ?", new StringParameter(category));
        return fluxMany(query.build()).map(this::toModel);
    }

    public Mono<Void> saveCredential(Credential credential) {
        if (credential.getId() == null) {
            return createCredential(credential).then();
        } else {
            return updateCredential(credential).then();
        }
    }

    private Mono<Integer> createCredential(Credential credential) {
       var query = """
            INSERT INTO `credential` (`category`, `key`, `value`, `user_id`, `guild_id`)
            VALUES (?, ?, ?, ?, ?)
        """;
        return monoExecute(query,
            new StringParameter(credential.getCategory()),
            new StringParameter(credential.getKey()),
            new StringParameter(EncryptionHelper.encrypt(credential.getValue())),
            Parameter.from(credential.getTarget().getUserId()),
            Parameter.from(credential.getTarget().getGuildId())
        );
    }

    private Mono<Integer> updateCredential(Credential credential) {
        var query = "UPDATE `credential` SET `value` = ? WHERE `id` = ?";
        return monoExecute(query,
            new StringParameter(EncryptionHelper.encrypt(credential.getValue())),
            new IntParameter(credential.getId())
        );
    }

}