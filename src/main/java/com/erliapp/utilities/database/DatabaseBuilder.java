package com.erliapp.utilities.database;

import com.erliapp.utilities.PropertiesEx;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

/**
 * Database Builder. A Utility to create a Database with proper formatting.
 * Makes it easier, so you don't need specific configuration!
 */
public class DatabaseBuilder {

  private LinkedHashMap<String, LinkedHashMap<String, String>> setup;
  private PropertiesEx properties;

  private static final String[] dataTypes = {"BIGINT", "TEXT"};
  private static final String[] databaseTypes = {"cassandra", "sqlite", "mariadb", "mysql"};
  private Path sqlitePath = Paths.get("./");
  private boolean typeSet = false;
  private boolean keyspaceSet = false;

  /**
   * Default Constructor. Runs main constructor, and initialises Properties as default.
   */
  public DatabaseBuilder() {
    this(new PropertiesEx());
  }

  /**
   * Primary Constructor. Sets up data, with a pre-set Properties.
   *
   * @param properties Configuration for Database (with or without data.)
   */
  public DatabaseBuilder(PropertiesEx properties) {
    setup = new LinkedHashMap<>();
    this.properties = properties;

    if (properties.containsKey("cqlkeyspace")) {
      keyspaceSet = true;
    }
    if (properties.containsKey("databaseType")) {
      typeSet = true;
    }
  }

  /**
   * Adds a possible Row and column to a table in a database.
   *
   * @param table Table within database to use.
   * @param key Name of column.
   * @param dataType Data Type. Can either be {@code text} or {@code bigint}.
   * @throws IllegalArgumentException if any of the parameters are either null or an empty String.
   */
  public void addDatabaseValue(
      String table,
      String key,
      String dataType
  ) throws IllegalArgumentException {
    if (table.equals("") || table == null) {
      throw new IllegalArgumentException("Table must have a valid name!");
    }
    if (!setup.containsKey(table)) {
      setup.put(table, new LinkedHashMap<>());
    }

    if (key.equals("") || key == null) {
      throw new IllegalArgumentException("Key must have a value!");
    }

    boolean foundDataType = false;

    for (String type : dataTypes) {
      if (type.equals(dataType)) {
        foundDataType = true;
      }
    }

    if (foundDataType) {
      setup.get(table).put(key, dataType);
      return;
    }

    throw new IllegalArgumentException("Data Type is invalid!");
  }

  /**
   * Sets a Database's Type.
   *
   * @param type Database Type. Can be either {@code sqlite} or {@code cassandra}.
   * @throws IllegalArgumentException if Database Type is not supported by the builder.
   */
  public void setType(String type) throws IllegalArgumentException {
    boolean found = false;
    type = type.toLowerCase();
    for (String val : databaseTypes) {
      if (val.equals(type)) {
        found = true;
      }
    }

    if (found) {
      properties.setProperty("databaseType", type);
      typeSet = true;
    } else {
      throw new IllegalArgumentException("Database Type is not supported!");
    }
  }

  /**
   * Sets the Contact for a remote Database.
   *
   * @param contact Contact for Database.
   */
  public void setContact(String contact) {
    properties.setProperty("cqlcontact", contact);
  }

  /**
   * Sets the Datacenter for a Cassandra Database.
   *
   * @param datacenter Cassandra Datacenter.
   */
  public void setDatacenter(String datacenter) {
    properties.setProperty("cqldatacenter", datacenter);
  }

  /**
   * Sets the name of the Database. Keyspace for Cassandra, File name for Sqlite.
   *
   * @param keyspace Keyspace to set
   */
  public void setKeyspace(String keyspace) {
    keyspaceSet = true;
    properties.setProperty("cqlkeyspace", keyspace);
  }

  /**
   * Sets the official port for a database.
   *
   * @param port The port the database is hosted on.
   */
  public void setPort(int port) {
    properties.setProperty("cqlport", "" + port);
  }

  /**
   * Sets a Username to use Credentials to log in.
   *
   * @param username Username for Database.
   */
  public void setUser(String username) {
    properties.setProperty("cqluser", username);
  }

  /**
   * Sets a Password to log in with.
   *
   *  @param password User Password.
   */
  public void setPassword(String password) {
    properties.setProperty("cqlpassword", password);
  }

  /**
   * Sets whether Authentication is necessary for a database.
   *
   *  @param useAuth {@code true} if auth is used. {@code false} if it isn't.
   */
  public void setUseAuth(boolean useAuth) {
    properties.setProperty("cqlauth", "" + useAuth);
  }

  /**
   * Sets the path for SQLite to use, instead of the default directory.
   *
   * @param path The path to set.
   */
  public void setSqlitePath(Path path) {
    this.sqlitePath = path;
  }
  /**
   * Takes a Database Configuration, and builds a new Database.
   *
   *  @return A new Database
   * @throws IllegalStateException if any configuration is invalid for usage.
   * @throws UnknownHostException if the database can't connect to Cassandra.
   */
  public Database build() throws IllegalStateException, UnknownHostException {
    if (!typeSet) {
      throw new IllegalStateException("Can't build a database without a type.");
    }

    if (!keyspaceSet) {
      throw new IllegalStateException("Keyspace must be set.");
    }

    boolean auth = Boolean.parseBoolean(properties.getProperty("cqlauth", "false"));

    if (auth) {
      if (properties.getProperty("cqluser").equals("")) {
        throw new IllegalStateException("User must be set if using authentication.");
      }
      if (properties.getProperty("cqlpassword").equals("")) {
        throw new IllegalStateException("Password must be set if using authentication.");
      }
    }

    if (setup.size() == 0) {
      throw new IllegalStateException("Setup must have a value.");
    }

    String type = properties.getProperty("databaseType");

    Database out;
    if (type.equals("cassandra")) {
      if (
          properties.containsKey("cqlcontact")
              && properties.containsKey("cqldatacenter")
              && properties.containsKey("cqlport")
      ) {
        out = new CassandraDatabase(setup, properties);
      } else {
        throw new IllegalStateException(
            "Cassandra Database Requires a Contact, Datacenter, and Port!"
        );
      }
    } else if (type.equals("sqlite")) {
      SqliteDatabase temp = new SqliteDatabase(setup, properties, sqlitePath);
      temp.create();
      out = temp;
    } else if (type.equals("mysql")) {
      SqlDatabase temp = new SqlDatabase(setup, properties);
      temp.create();
      out = temp;
    } else if (type.equals("mariadb")) {
      MariaDBDatabase temp = new MariaDBDatabase(setup, properties);
      temp.create();
      out = temp;
    } else {
      throw new IllegalStateException("Database Type not allowed!");
    }
    return out;
  }

}
