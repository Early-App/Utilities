package com.erliapp.utilities.database;

import com.erliapp.utilities.PropertiesEx;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/** MariaDB Database System. */
public class MariaDBDatabase extends SqliteDatabase {

  /**
   * Constructor.
   *
   * @param databases Database Setup.
   * @param prop Main Properties.
   */
  public MariaDBDatabase(
      LinkedHashMap<String, LinkedHashMap<String, String>> databases, PropertiesEx prop, Path path) {
    super(databases, prop, path, "mariadb");

  }
}
