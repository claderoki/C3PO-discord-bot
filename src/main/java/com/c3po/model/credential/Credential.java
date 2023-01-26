package com.c3po.model.credential;

import com.c3po.core.ScopeTarget;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.util.annotation.Nullable;


@RequiredArgsConstructor
@Getter
public class Credential {
    private final @Nullable Integer id;
    private final String category;
    private final String key;
    private final String value;
    private final ScopeTarget target;
}
