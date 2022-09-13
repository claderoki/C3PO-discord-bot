import com.c3po.core.command.Context;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.MenuOption;
import com.c3po.ui.input.base.Interactor;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import menu.MenuOptionSettings;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MenuTest {

    private void baseTest(List<MenuOptionSettings> optionSettings) {
        Context context = mock(Context.class);
        ChatInputInteractionEvent event = mock(ChatInputInteractionEvent.class);
        when(context.getEvent()).thenReturn(event);
        GatewayDiscordClient client = mock(GatewayDiscordClient.class);
        ArrayList<MenuOption<?,?,?>> options = new ArrayList<>();
        ArrayList<ComponentInteractionEvent> events = new ArrayList<>();
        User user = mock(User.class);
        when(user.getId()).thenReturn(Snowflake.of(1));

        int amountToHandle = 0;
        boolean stop = false;
        for(MenuOptionSettings settings: optionSettings) {
            MenuOption option = mock(MenuOption.class);
            when(option.getCustomId()).thenReturn(settings.getCustomId());
            if (settings.isExecute()) {
                ComponentInteractionEvent optionEvent = mock(ComponentInteractionEvent.class);
                when(optionEvent.getCustomId()).thenReturn(settings.getCustomId());
                Interaction menuInteraction = mock(Interaction.class);
                when(menuInteraction.getUser()).thenReturn(user);
                when(optionEvent.getInteraction()).thenReturn(menuInteraction);
                when(option.isAllowed(optionEvent)).thenReturn(true);
                when(option.execute(any())).thenReturn(Mono.empty());
                when(option.shouldContinue()).thenReturn(settings.isShouldContinue());
                events.add(optionEvent);
                if (!stop) {
                    amountToHandle++;
                }
                if (!settings.isShouldContinue()) {
                    stop = true;
                }
            }
            options.add(option);
        }

        when(client.on(ComponentInteractionEvent.class)).thenReturn(Flux.fromIterable(events));
        when(event.getClient()).thenReturn(client);
        Interaction interaction = mock(Interaction.class);
        when(interaction.getUser()).thenReturn(user);
        when(event.getInteraction()).thenReturn(interaction);
        when(context.getEvent()).thenReturn(event);
        Menu menu = new Menu(context);
        options.forEach(menu::addOption);
        Interactor interactor = mock(Interactor.class);
        when(interactor.replyOrEdit(any(), any())).thenReturn(Mono.just(mock(Message.class)));
        MenuManager<Menu> menuManager = new MenuManager<>(menu, interactor);
        menuManager.waitFor().block();

        if (menu.getOptionsHandled() != amountToHandle) {
            throw new RuntimeException("failed");
        }
    }

    @Test
    public void testSingle() {
        baseTest(List.of(
            MenuOptionSettings.builder()
                .customId("1")
                .build()
        ));
    }

    @Test
    public void testMulti() {
        int i = 1;
        baseTest(List.of(
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .execute(true)
                .build(),
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .build(),
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .execute(false)
                .build()
        ));
    }

    @Test
    public void testMulti2() {
        int i = 1;
        baseTest(List.of(
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .build(),
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .build(),
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .execute(false)
                .build(),
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .shouldContinue(false)
                .build(),
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .build(),
            MenuOptionSettings.builder()
                .customId(String.valueOf(i++))
                .build()
        ));
    }
}
