import com.c3po.core.command.Command;
import com.c3po.core.command.CommandSettingValidation;
import com.c3po.core.command.CommandSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.PermissionSet;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static reactor.core.publisher.Mono.when;

public class CommandTest {
    @Test
    public void abc() {
        Command command = mock(Command.class);
        when(c -> command.getSettings()).thenReturn(CommandSettings.builder().build());
        Member member = mock(Member.class);
        when(member.getBasePermissions()).thenReturn(Mono.just(PermissionSet.all()));
        Boolean validated = CommandSettingValidation.validate(command.getSettings(), Optional.of(member), Optional.of(Snowflake.of(1))).block();
        assertEquals(Boolean.TRUE, validated);
    }
}
