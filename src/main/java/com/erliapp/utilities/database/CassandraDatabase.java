package com.erliapp.utilities.database;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.erliapp.utilities.PropertiesEx;
import com.erliapp.utilities.Util;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

/** Cassandra Database instance. Connects to a Cassandra Database to get information. */
public class CassandraDatabase implements Database {

  private final LinkedHashMap<String, LinkedHashMap<String, String>> databases;

  private final CqlSession session;

  /**
   * Constructor.
   *
   * @param databases General information about a database
   * @param prop Database Configuration
   * @throws UnknownHostException If a host is not found.
   */
  public CassandraDatabase(
      LinkedHashMap<String, LinkedHashMap<String, String>> databases, PropertiesEx prop)
      throws UnknownHostException {
    this.databases = databases;
    String ipAddress = prop.getProperty("cqlcontact");
    if (!Util.isNumeric(prop.getProperty("cqlcontact").substring(0, 2))) {
      InetAddress ipAddressInet = InetAddress.getByName(ipAddress);
      ipAddress = ipAddressInet.getHostAddress();
    }
    if (prop.getProperty("cqlauth").equals("true")) {
      session =
          CqlSession.builder()
              .addContactPoint(
                  new InetSocketAddress(ipAddress, Integer.parseInt(prop.getProperty("cqlport"))))
              .withAuthCredentials(prop.getProperty("cqluser"), prop.getProperty("cqlpassword"))
              .withLocalDatacenter(prop.getProperty("cqldatacenter"))
              .withKeyspace(prop.getProperty("cqlkeyspace"))
              .build();
    } else {
      session =
          CqlSession.builder()
              .addContactPoint(
                  new InetSocketAddress(ipAddress, Integer.parseInt(prop.getProperty("cqlport"))))
              .withLocalDatacenter(prop.getProperty("cqldatacenter"))
              .withKeyspace(prop.getProperty("cqlkeyspace"))
              .build();
    }
  }

  /**
   * Runs a CQL SELECT statement, and returns a list of data.
   *
   * @param selecting Columns to select in the database
   * @param database Title of the table being selected from
   * @param where Anything after a selection, could be a WHERE statement, or a LIMIT statement
   * @return A List of rows, with each column within.
   */
  @Override
  public DatabaseSelection select(
      String[] selecting, String database, String where) {

    // Gather items that are selected, and output as a String with commas
    StringBuilder selected = new StringBuilder();
    for (String item : selecting) {
      selected.append(item).append(", ");
    }
    selected = new StringBuilder(selected.substring(0, selected.length() - 2));

    // Take Where statement, and allow it to work.
    String whereStatement = where;
    if (whereStatement.length() == 0) {
      whereStatement = "";
    } else if (whereStatement.startsWith("LIMIT")) {
      whereStatement = " " + whereStatement;
    } else {
      whereStatement = " WHERE " + whereStatement;
    }

    // Allow Filtering allows data to be gathered from multiple servers.
    if (whereStatement.contains("LIMIT")) {
      whereStatement += " ALLOW FILTERING";
    }

    // Actually executes the program
    ResultSet rs =
        session.execute("SELECT " + selected + " FROM " + database + " " + whereStatement);

    // Gets table information, and starts to parse and output data in their correct datatypes.
    LinkedHashMap<String, String> table = databases.get(database);
    DatabaseSelection out = new DatabaseSelection();
    for (Row row : rs.all()) {
      out.addRow();
      for (String i : selecting) {
        if (table.get(i).equalsIgnoreCase("bigint")) {
          out.addItem(i, new com.erliapp.utilities.database.Row<>(Long.class, row.getLong(i)));
        } else if (table.get(i).equalsIgnoreCase("text")) {
          out.addItem(i, new com.erliapp.utilities.database.Row<>(String.class, row.getString(i)));
        } else if (table.get(i).equalsIgnoreCase("double")) {
          out.addItem(i, new com.erliapp.utilities.database.Row<>(Double.class, row.getDouble(i)));
        } else if (table.get(i).equalsIgnoreCase("uuid")) {
          out.addItem(i, new com.erliapp.utilities.database.Row<>(UUID.class, row.getUuid(i)));
        }
      }
    }

    return out;
  }

  /**
   * Runs any non-data request to a database. Does not return any data, and does not insert a new
   * row.
   *
   * @param query Query to run.
   */
  @Override
  public void update(String query) {
    session.execute(query);
  }

  /**
   * Runs the CQL Insert command.
   *
   * @param database Table to insert into
   * @param inserting List of columns to insert
   * @param values Any values being inserted.
   */
  @Override
  public void insert(String database, String[] inserting, Object... values) {

    // Gather data to insert as a string
    String query = DatabaseUtil.setupInsertingData(database, inserting);

    // Runs insert statement
    PreparedStatement prepared1 = session.prepare(query);
    BoundStatement bound = (prepared1.bind(values));
    session.execute(bound);
  }

  /** Closes the connection to the database. */
  @Override
  public void close() {
    session.close();
  }
}
