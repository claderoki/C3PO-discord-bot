package com.c3po.ui.pagination;

import java.util.List;

public record Page<T>(int identifier, List<T> values) {
}
