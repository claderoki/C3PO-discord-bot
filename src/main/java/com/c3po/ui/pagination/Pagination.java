package com.c3po.ui.pagination;

import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public abstract class Pagination<T> {
    private final Map<Integer, Page<T>> pages = new HashMap<>();
    private final List<String> headers = new ArrayList<>();
    private int totalPages;
    private int itemsPerPage = 20;
    private int currentPage = 0;

    protected abstract int getCount();

    final public Page<T> getPage(int page) {
        return pages.computeIfAbsent(page, c -> getUncachedPage(page));
    }

    protected abstract Page<T> getUncachedPage(int page);

    protected abstract int getAllUncachedPages();
}
