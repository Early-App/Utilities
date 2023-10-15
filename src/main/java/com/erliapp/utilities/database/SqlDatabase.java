package com.erliapp.utilities.database;

import com.erliapp.utilities.PropertiesEx;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/** Sql Database System. */
public class SqlDatabase extends SqliteDatabase {

  /**
   * Constructor.
   *
   * @param databases Database Setup.
   * @param prop Main Properties.
   */
  public SqlDatabase(
      LinkedHashMap<String, LinkedHashMap<String, String>> databases, PropertiesEx prop, Path path) {
    super(databases, prop, path, "mysql");

  }
}
