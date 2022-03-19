package com.c3po.command.personalrole;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.Scope;
import com.c3po.core.command.Context;
import com.c3po.core.property.PropertyValue;
import com.c3po.errors.PublicException;
import com.c3po.helper.ColorFormatter;
import com.c3po.helper.EmbedHelper;
import com.c3po.model.personalrole.PersonalRoleSettings;
import com.c3po.service.AttributeService;
import com.c3po.service.PersonalRoleService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.RoleCreateSpec;
import discord4j.core.spec.RoleEditSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PersonalRoleProcessor {
    public static Integer personalRoleAttributeId = AttributeService.getId("personal_role");

    private final PersonalRoleType type;
    private final String rawValue;
    private final Context context;

    private PersonalRoleSettings settings;
    private PropertyValue personalRoleAttributeValue;
    private Role existingRole;
    private Guild guild;
    private Member member;

    private void load() {
        settings = PersonalRoleService.getSettings(context.getTarget(Scope.GUILD));
        personalRoleAttributeValue = AttributeRepository.db()
            .getHydratedPropertyValue(context.getTarget(Scope.MEMBER), personalRoleAttributeId)
            .orElseThrow();

        guild = context.getEvent().getInteraction().getGuild().blockOptional().orElseThrow();
        if (personalRoleAttributeValue.getValue() != null) {
            existingRole = guild.getRoleById(Snowflake.of(Long.parseLong(personalRoleAttributeValue.getValue()))).block();
        }
        member = context.getEvent().getInteraction().getMember().orElseThrow();
    }

    private void validate() {
        if (personalRoleAttributeValue.getValue() != null && existingRole == null) {
            AttributeRepository.db().delete(personalRoleAttributeValue);
            throw new PublicException("Your personal role doesn't exist anymore. I removed it. Please try again.");
        }

        if (!settings.isEnabled()) {
            throw new PublicException("Not enabled.");
        }

        Long roleIdNeeded = settings.getRoleId();

        Member member = context.getEvent().getInteraction().getMember().orElseThrow();
        if (roleIdNeeded != null && member.getRoleIds().contains(Snowflake.of(roleIdNeeded))) {
            throw new PublicException("You do not have the required role for this.");
        }

    }

    private Mono<Role> createRole() {
        RoleCreateSpec.Builder roleSpec = RoleCreateSpec.builder();
        switch (type) {
            case NAME -> roleSpec.name(rawValue).color(Color.BLUE);
            case COLOR -> roleSpec.color(ColorFormatter.parse(rawValue))
                .name(member.getDisplayName());
        }

        return guild.createRole(roleSpec.build()).flatMap(role -> {
            personalRoleAttributeValue.setValue(role.getId().asString());
            AttributeRepository.db().save(personalRoleAttributeValue);
            role.changePosition(PersonalRoleService.getRolePosition(guild)).subscribe();
            return Mono.just(role);
        });
    }

    private Mono<Void> editRole() {
        RoleEditSpec.Builder roleSpec = RoleEditSpec.builder();
        switch (type) {
            case NAME -> roleSpec.name(rawValue);
            case COLOR -> roleSpec.color(ColorFormatter.parse(rawValue));
        }
        return existingRole.edit(roleSpec.build()).then();
    }

    public Mono<?> execute() {
        load();

        if (type.equals(PersonalRoleType.DELETE) && existingRole != null) {
            return existingRole.delete().flatMap(c -> {
                AttributeRepository.db().delete(personalRoleAttributeValue);
                return context.getEvent().reply().withEmbeds(EmbedHelper.normal("Okay, role has been deleted.").build());
            });
        }

        validate();

        if (existingRole == null) {
            return createRole().flatMap(role -> member.addRole(role.getId())).then(
                context.getEvent().reply().withEmbeds(EmbedHelper.normal("Okay, role has been created.").build()));
        } else {
            return editRole().flatMap(role -> {
                if (!member.getRoleIds().contains(existingRole.getId())) {
                    member.addRole(existingRole.getId());
                }
                return Mono.empty();
            }).then(
                context.getEvent().reply().withEmbeds(EmbedHelper.normal("Okay, role has been edited.").build()));
        }
    }
}
