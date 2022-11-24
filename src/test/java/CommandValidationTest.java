import com.c3po.core.command.Command;
import com.c3po.core.command.CommandSettingValidation;
import com.c3po.core.command.CommandSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.PermissionSet;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandValidationTest {
    @Test
    public void normalCommandForAllPerms() {
        Command command = mock(Command.class);
        when(command.getSettings()).thenReturn(CommandSettings.builder().build());
        Member member = mock(Member.class);
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.all()));
        Boolean validated = CommandSettingValidation.validate(command.getSettings(), member, Snowflake.of(1)).block();
        assertEquals(Boolean.TRUE, validated);
    }

    @Test
    public void adminCommandForNoPerms() {
        Command command = mock(Command.class);
        when(command.getSettings()).thenReturn(CommandSettings.builder().adminOnly(true).build());
        Member member = mock(Member.class);
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.none()));
        Boolean validated = CommandSettingValidation.validate(command.getSettings(), member, Snowflake.of(1)).block();
        assertEquals(Boolean.FALSE, validated);
    }

    @Test
    public void adminCommandForAllPerms() {
        Command command = mock(Command.class);
        when(command.getSettings()).thenReturn(CommandSettings.builder().adminOnly(true).build());
        Member member = mock(Member.class);
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.all()));
        Boolean validated = CommandSettingValidation.validate(command.getSettings(), member, Snowflake.of(1)).block();
        assertEquals(Boolean.TRUE, validated);
    }

    @Test
    public void guildCommandInDM() {
        Command command = mock(Command.class);
        when(command.getSettings()).thenReturn(CommandSettings.builder().guildOnly(true).build());
        Member member = mock(Member.class);
        Boolean validated = CommandSettingValidation.validate(command.getSettings(), member, null).block();
        assertEquals(Boolean.FALSE, validated);
    }

    @Test
    public void guildCommandInGuild() {
        Command command = mock(Command.class);
        when(command.getSettings()).thenReturn(CommandSettings.builder().guildOnly(true).build());
        Member member = mock(Member.class);
        Boolean validated = CommandSettingValidation.validate(command.getSettings(), member, Snowflake.of(1)).block();
        assertEquals(Boolean.TRUE, validated);
    }
}
