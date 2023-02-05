import com.c3po.core.command.validation.*;
import com.c3po.error.PublicException;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.PermissionSet;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandValidationTest {
    private boolean validate(ChatInputInteractionEvent event, List<CommandValidation> validations) {
        try {
            Boolean valid = new CommandValidator().validate(event, validations).block();
            return Boolean.TRUE.equals(valid);
        } catch (PublicException e) {
            return false;
        }
    }

    @Test
    public void normalCommandForAllPerms() {
        ChatInputInteractionEvent event = mock(ChatInputInteractionEvent.class);
        Interaction interaction = mock(Interaction.class);
        Member member = mock(Member.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getMember()).thenReturn(Optional.of(member));
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.all()));
        Boolean validated = validate(event, List.of());
        assertEquals(Boolean.TRUE, validated);
    }

    @Test
    public void adminCommandForNoPerms() {
        ChatInputInteractionEvent event = mock(ChatInputInteractionEvent.class);
        Interaction interaction = mock(Interaction.class);
        Member member = mock(Member.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getMember()).thenReturn(Optional.of(member));
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.none()));
        Boolean validated = validate(event, List.of(new IsAdmin()));
        assertEquals(Boolean.FALSE, validated);
    }

    @Test
    public void adminCommandForAllPerms() {
        ChatInputInteractionEvent event = mock(ChatInputInteractionEvent.class);
        Interaction interaction = mock(Interaction.class);
        Member member = mock(Member.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getMember()).thenReturn(Optional.of(member));
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.all()));
        Boolean validated = validate(event, List.of(new IsAdmin()));
        assertEquals(Boolean.TRUE, validated);
    }

    @Test
    public void guildCommandInDM() {
        ChatInputInteractionEvent event = mock(ChatInputInteractionEvent.class);
        Interaction interaction = mock(Interaction.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getGuildId()).thenReturn(Optional.empty());
        Boolean validated = validate(event, List.of(new GuildOnly()));
        assertEquals(Boolean.FALSE, validated);
    }

    @Test
    public void guildCommandInGuild() {
        ChatInputInteractionEvent event = mock(ChatInputInteractionEvent.class);
        Interaction interaction = mock(Interaction.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getGuildId()).thenReturn(Optional.of(Snowflake.of(1)));
        Boolean validated = validate(event, List.of(new GuildOnly()));
        assertEquals(Boolean.TRUE, validated);
    }

    @Test
    public void adminAndGuildOnlyValid() {
        ChatInputInteractionEvent event = mock(ChatInputInteractionEvent.class);
        Interaction interaction = mock(Interaction.class);
        Member member = mock(Member.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getMember()).thenReturn(Optional.of(member));
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.all()));
        when(interaction.getGuildId()).thenReturn(Optional.of(Snowflake.of(1)));
        Boolean validated = validate(event, List.of(new GuildOnly(), new IsAdmin()));
        assertEquals(Boolean.TRUE, validated);
    }


    @Test
    public void adminAndGuildOnlyInvalid() {
        ChatInputInteractionEvent event = mock(ChatInputInteractionEvent.class);
        Interaction interaction = mock(Interaction.class);
        Member member = mock(Member.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getMember()).thenReturn(Optional.of(member));
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.none()));
        when(interaction.getGuildId()).thenReturn(Optional.of(Snowflake.of(1)));
        Boolean validated = validate(event, List.of(new GuildOnly(), new IsAdmin()));
        assertEquals(Boolean.FALSE, validated);
    }
}
