package com.erliapp.utilities.database;

import java.util.List;
import java.util.Map;

/** Database interface. Allows an application to access data from an external source */
public interface Database {

  /**
   * Makes a database selection
   *
   * @param selecting String array of items to select
   * @param database Database to select from.
   * @param where General WHERE statement.
   * @return Database Data.
   */
  List<Map<String, Row>> select(String[] selecting, String database, String where);


  /**
   * Runs a blank query.
   * @param query Query to run.
   */
  void update(String query);


  /**
   * Runs an INSERT statement
   * @param database Database to Insert into
   * @param inserting Data Keys to insert
   * @param values Values being inserted.
   */
  void insert(String database, String[] inserting, Object... values);

  /**
   * Closes a database connection.
   */
  void close();
}
