package com.c3po.command.personalrole;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.command.CommandCategory;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.service.AttributeService;
import com.c3po.service.PersonalRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public abstract class PersonalRoleSubCommand extends SubCommand {
    @Autowired
    protected PersonalRoleService personalRoleService;

    @Autowired
    protected AttributeService attributeService;

    @Autowired
    protected AttributeRepository attributeRepository;

    protected abstract PersonalRoleProcessor getProcessor(Context context);

    protected PersonalRoleSubCommand(String name, String description) {
        super(CommandCategory.PERSONAL_ROLE, name, description);
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        PersonalRoleProcessor processor = getProcessor(context);
        processor.setAutos(personalRoleService, attributeService, attributeRepository);
        return processor.execute();
    }
}
