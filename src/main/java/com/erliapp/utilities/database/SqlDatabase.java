package com.erliapp.utilities.database;

import com.erliapp.utilities.PropertiesEx;
import com.erliapp.utilities.Util;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
      LinkedHashMap<String, LinkedHashMap<String, String>> databases, PropertiesEx prop)
      throws UnknownHostException {
    super(
        databases,
        "jdbc://mysql://"
            + prop.getProperty("cqlcontact")
            + ":"
            + prop.getProperty("cqlport")
            + "/"
            + prop.getProperty("cqlkeyspace")
            + (prop.getProperty("cqlauth").equals("true")
            ? "?user=" + prop.getProperty("cqluser")
            + "&password="
            + prop.getProperty("cqlpassword")
            : "" )
    );

  }
}
