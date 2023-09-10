package com.erliapp.utilities.database;

/** Database Utility Functions. */
public class DatabaseUtil {

  /**
   * Creates Data to Insert.
   *
   * @param database Table to insert into
   * @param inserting Columns inserting into
   * @return Insert Statment, with Formated Data.
   */
  protected static String setupInsertingData(String database, String[] inserting) {
    StringBuilder selected = new StringBuilder();
    for (String item : inserting) {
      selected.append(item).append(", ");
    }
    selected = new StringBuilder(selected.substring(0, selected.length() - 2));

    StringBuilder valueQuestion = new StringBuilder();
    valueQuestion.append("?,".repeat(inserting.length));
    valueQuestion = new StringBuilder(valueQuestion.substring(0, valueQuestion.length() - 1));

    return "INSERT INTO " + database + "(" + selected + ") VALUES(" + valueQuestion + ");";
  }
}
