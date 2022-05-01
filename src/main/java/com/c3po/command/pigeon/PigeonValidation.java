package com.c3po.command.pigeon;

import com.c3po.connection.repository.PigeonRepository;
import com.c3po.errors.PublicException;
import com.c3po.model.pigeon.PigeonStatus;
import com.c3po.service.HumanService;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PigeonValidation {
    private int goldNeeded;
    private PigeonStatus requiredPigeonStatus;
    private Boolean needsActivePigeon;
    private boolean needsPvpEnabled;
    private boolean needsAvailablePvpAction;
    private boolean other;
    private Integer humanId;

    public PigeonValidationResult validate(long userId) throws PublicException {
        if (humanId == null) {
            humanId = HumanService.getHumanId(userId);
        }
        PigeonValidationResult result = PigeonRepository.db().getValidationResult(this);

        if (result.isShouldNotifyDeath() && !other) {
             PigeonRepository.db().setDeathNotified(result.getPigeonId());
            throw new PublicException("Your pigeon has died. Better take better care of it next time!");
        }

        if (needsPvpEnabled && !result.isHasPvpEnabled()) {
            if (other) {
                throw new PublicException("The other persons pigeon does not have PvP enabled.");
            } else {
                throw new PublicException("Your pigeon does not have PvP enabled.");
            }
        }

        if (needsAvailablePvpAction && !result.isHasAvailablePvpAction()) {
            if (other) {
                throw new PublicException("The other persons pigeon does not have an available PvP action yet.");
            } else {
                throw new PublicException("Your pigeon does not have an available PvP action yet.");
            }
        }

        if (goldNeeded > 0 && !result.isHasGoldNeeded()) {
            if (other) {
                throw new PublicException("The other person needs %s gold to perform this action.".formatted(goldNeeded));
            } else {
                throw new PublicException("You need %s gold to perform this action.".formatted(goldNeeded));
            }
        }

        if (needsActivePigeon != null && needsActivePigeon != result.isHasActivePigeon()) {
            if (result.isHasActivePigeon()) {
                if (other) {
                    throw new PublicException("The other person already has a pigeon!");
                } else {
                    throw new PublicException("You already have a pigeon!");
                }
            } else {
                if (other) {
                    throw new PublicException("The other person does not have a pigeon!");
                } else {
                    throw new PublicException("You do not have a pigeon!");
                }
            }
        }

        if (requiredPigeonStatus != null && !result.isHasRequiredStatus()) {
            if (other) {
                throw new PublicException("The other pigeon needs to be %s to perform this action.".formatted(requiredPigeonStatus.toString()));
            } else {
                throw new PublicException("Your pigeon needs to be %s to perform this action.".formatted(requiredPigeonStatus.toString()));
            }
        }
        return result;
    }
}
