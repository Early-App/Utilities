package com.erliapp.utilities.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseSelection {

    private final List<Map<String,Row<?>>> data;

    public DatabaseSelection() {
        this.data = new ArrayList<>();
    }

    public List<Map<String,Row<?>>> getData() {
        return data;
    }

    public void addRow(int i, Map<String,Row<?>> row) {
        data.add(i, row);
    }

    public void addRow(int i) {
        addRow(i, new LinkedHashMap<>());
    }

    public void addRow(Map<String,Row<?>> row) {
        data.add(row);
    }

    public void addRow() {
        addRow(new LinkedHashMap<>());
    }

    public void addItem(int row, String key, Row<?> value) {
        data.get(row).put(key, value);
    }

    public void addItem(String key, Row<?> value) {
        data.get(data.size()-1).put(key, value);
    }

    public Map<String,Row<?>> getRow(int i) {
        return data.get(i);
    }

    public Row<?> getValue(int i, String key) {
        return data.get(i).get(key);
    }

    public int size() {
        return data.size();
    }



}
