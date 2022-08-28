package com.c3po.command.pigeon.validation;

import com.c3po.connection.repository.PigeonRepository;
import com.c3po.error.PublicException;
import com.c3po.service.HumanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PigeonValidation {
    @Autowired
    private HumanService humanService;

    @Autowired
    private PigeonRepository pigeonRepository;

    public PigeonValidationResult validate(PigeonValidationSettings settings, Long userId) throws PublicException {
        int humanId = humanService.getHumanId(userId);
        PigeonValidationData data = pigeonRepository.getValidationData(settings, humanId);

        if (data.isShouldNotifyDeath() && !settings.isOther()) {
            pigeonRepository.setDeathNotified(data.getPigeonId());
            throw new PublicException("Your pigeon has died. Better take better care of it next time!");
        }

        if (settings.isNeedsPvpEnabled() && !data.isHasPvpEnabled()) {
            if (settings.isOther()) {
                throw new PublicException("The other persons pigeon does not have PvP enabled.");
            } else {
                throw new PublicException("Your pigeon does not have PvP enabled.");
            }
        }

        if (settings.isNeedsAvailablePvpAction() && !data.isHasAvailablePvpAction()) {
            if (settings.isOther()) {
                throw new PublicException("The other persons pigeon does not have an available PvP action yet.");
            } else {
                throw new PublicException("Your pigeon does not have an available PvP action yet.");
            }
        }

        if (settings.getGoldNeeded() > 0 && !data.isHasGoldNeeded()) {
            if (settings.isOther()) {
                throw new PublicException("The other person needs %s gold to perform this action.".formatted(settings.getGoldNeeded()));
            } else {
                throw new PublicException("You need %s gold to perform this action.".formatted(settings.getGoldNeeded()));
            }
        }

        if (settings.getNeedsActivePigeon() != null && settings.getNeedsActivePigeon() != data.isHasActivePigeon()) {
            if (data.isHasActivePigeon()) {
                if (settings.isOther()) {
                    throw new PublicException("The other person already has a pigeon!");
                } else {
                    throw new PublicException("You already have a pigeon!");
                }
            } else {
                if (settings.isOther()) {
                    throw new PublicException("The other person does not have a pigeon!");
                } else {
                    throw new PublicException("You do not have a pigeon!");
                }
            }
        }

        if (settings.getRequiredPigeonStatus() != null && !data.isHasRequiredStatus()) {
            if (settings.isOther()) {
                throw new PublicException("The other pigeon needs to be %s to perform this action.".formatted(settings.getRequiredPigeonStatus().toString()));
            } else {
                throw new PublicException("Your pigeon needs to be %s to perform this action.".formatted(settings.getRequiredPigeonStatus().toString()));
            }
        }
        return new PigeonValidationResult(data.getPigeonId(), data.getHumanId());
    }

}
