package menu;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MenuOptionSettings {
    private String customId;
    @Builder.Default
    private boolean execute = true;
    @Builder.Default
    private boolean shouldContinue = true;
}
