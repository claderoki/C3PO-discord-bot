package com.c3po.ui.table;

import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Table {
    private final ArrayList<Row> rows = new ArrayList<>();

    public void addRow(Row row) {
        rows.add(row);
    }
}
