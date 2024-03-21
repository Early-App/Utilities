package com.erliapp.utilities.database;

import com.erliapp.utilities.PropertiesEx;
import java.net.UnknownHostException;
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
      LinkedHashMap<String, LinkedHashMap<String, String>> databases, PropertiesEx prop)
      throws UnknownHostException {
    super(
        databases,
        "jdbc:mysql://"
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

      public SqlDatabase(
        LinkedHashMap<String, LinkedHashMap<String, String>> databases, PropertiesEx prop, boolean ssl) {
        super(
            databases,
            "jdbc:mariadb://"
                + prop.getProperty("cqlcontact")
                + ":"
                + prop.getProperty("cqlport")
                + "/"
                + prop.getProperty("cqlkeyspace")
                + "?ssl=" + ssl
                + (prop.getProperty("cqlauth").equals("true")
                ? "&user=" + prop.getProperty("cqluser")
                + "&password="
                + prop.getProperty("cqlpassword")
                : "")
        );

      }
}
