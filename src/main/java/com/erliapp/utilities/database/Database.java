package com.erliapp.utilities.database;

import java.util.List;
import java.util.Map;

/** Database interface. Allows an application to access data from an external source */
public interface Database {

  List<Map<String, Row>> select(String[] selecting, String database, String where);

  void update(String query);

  void insert(String database, String[] inserting, Object... values);

  void close();
}
