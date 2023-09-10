package com.erliapp.utilities.database;

import com.erliapp.utilities.PropertiesEx;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Sqlite Database System. */
public class SqliteDatabase implements Database {

  private final String url;
  private final LinkedHashMap<String, LinkedHashMap<String, String>> databases;

  /**
   * Constructor.
   *
   * @param databases Database Setup.
   * @param prop Main Properties.
   */
  public SqliteDatabase(
      LinkedHashMap<String, LinkedHashMap<String, String>> databases, PropertiesEx prop) {
    Path path = Paths.get("./");
    url = "jdbc:sqlite:" + path.toAbsolutePath() + "/" + prop.getProperty("cqlkeyspace") + ".db";
    this.databases = databases;
  }

  /** Creates a and initializes the Database with valid tables. */
  public void create() {
    ArrayList<String> statements = new ArrayList<>();

    List<String> tables = new ArrayList<>(databases.keySet());

    for (String item : tables) {

      LinkedHashMap<String, String> table = databases.get(item);
      List<String> keys = new ArrayList<>(table.keySet());
      StringBuilder out = new StringBuilder("CREATE TABLE IF NOT EXISTS " + item + " (\n");
      for (String key : keys) {
        out.append("    ").append(key).append(" ").append(table.get(key)).append(",\n");
      }
      out = new StringBuilder(out.substring(0, out.length() - 2));
      out.append("\n");
      out.append(");");
      statements.add(out.toString());
    }

    try (Connection conn = DriverManager.getConnection(url)) {
      if (conn != null) {
        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("Driver name is: " + meta.getDriverName());
        System.out.println("A new database has been created.");

        for (String i : statements) {
          System.out.println(i);
          Statement stmt = conn.createStatement();
          stmt.execute(i);
          stmt.close();
        }
        conn.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Runs the SQL SELECT statement.
   *
   * @param selecting Columns to Select
   * @param database Table to select from
   * @param where SQL Where statement.
   * @return List of Rows and Columns
   */
  @Override
  public List<Map<String, Row>> select(String[] selecting, String database, String where) {

    try (Connection conn = DriverManager.getConnection(url)) {

      StringBuilder selected = new StringBuilder();
      for (String item : selecting) {
        selected.append(item).append(", ");
      }
      selected = new StringBuilder(selected.substring(0, selected.length() - 2));
      String whereStatement = where;
      if (whereStatement.length() == 0) {
        whereStatement = ";";
      } else if (whereStatement.startsWith("LIMIT")) {
        whereStatement = " " + whereStatement + ";";
      } else {
        whereStatement = " WHERE " + whereStatement + ";";
      }
      String query = "SELECT " + selected + " FROM " + database + whereStatement;
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);

      LinkedHashMap<String, String> table = databases.get(database);
      List<Map<String, Row>> out = new ArrayList<>();
      while (rs.next()) {
        Map<String, Row> obj = new LinkedHashMap<>();
        for (String i : selecting) {
          if (table.get(i).equals("bigint")) {
            obj.put(i, new Row(i, rs.getLong(i)));
          } else if (table.get(i).equals("text")) {
            obj.put(i, new Row(i, rs.getString(i)));
          }
        }
        out.add(obj);
      }

      stmt.close();
      conn.close();

      return out;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Runs a given SQL query with no output.
   *
   * @param query The SQL query to run.
   */
  @Override
  public void update(String query) {
    try (Connection conn = DriverManager.getConnection(url)) {
      Statement stmt = conn.createStatement();
      stmt.execute(query + ";");
      stmt.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Runs the SQL INSERT function.
   *
   * @param database Table to Insert into
   * @param inserting Columns to Insert
   * @param values Values to place in Columns
   */
  @Override
  public void insert(String database, String[] inserting, Object... values) {
    try (Connection conn = DriverManager.getConnection(url)) {

      String query = DatabaseUtil.setupInsertingData(database, inserting);
      PreparedStatement pstmt = conn.prepareStatement(query);
      LinkedHashMap<String, String> table = databases.get(database);
      Object[] valuesArr = Arrays.stream(values).toArray();
      for (int i = 0; i < inserting.length; i++) {
        if (table.get(inserting[i]).equals("bigint")) {
          pstmt.setLong(i + 1, (Long) valuesArr[i]);
        } else if (table.get(inserting[i]).equals("text")) {
          pstmt.setString(i + 1, (String) valuesArr[i]);
        }
      }
      pstmt.executeUpdate();
      pstmt.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /** Closes a SQL connection (Not used for this.) */
  @Override
  public void close() {}
}
