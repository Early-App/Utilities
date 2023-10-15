package com.erliapp.utilities.database;

import com.erliapp.utilities.PropertiesEx;
import java.net.UnknownHostException;
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
      LinkedHashMap<String, LinkedHashMap<String, String>> databases, PropertiesEx prop) {
    super(
        databases,
        "jdbc://mariadb://"
            + prop.getProperty("cqlcontact")
            + ":"
            + prop.getProperty("cqlport")
            + "/"
            + prop.getProperty("cqlkeyspace")
            + (prop.getProperty("cqlauth").equals("true")
            ? "?user=" + prop.getProperty("cqluser")
            + "&password="
            + prop.getProperty("cqlpassword")
            : "")
    );
  }
}
