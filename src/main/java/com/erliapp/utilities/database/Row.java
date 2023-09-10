package com.erliapp.utilities.database;

/** Handles a Row of data within a Database. */
public class Row {

  private final String itemString;
  private final Class type;
  private final String key;
  private long itemLong;

  /**
   * Row Constructor.
   *
   * @param key Row Key
   * @param item Row data as Long.
   */
  public Row(String key, long item) {
    this.key = key;
    this.itemLong = item;
    this.itemString = String.valueOf(item);
    type = Long.class;
  }

  /**
   * Row Constructor.
   *
   * @param key Row Key.
   * @param item Row Data as String.
   */
  public Row(String key, String item) {
    this.key = key;
    this.itemString = item;
    try {
      this.itemLong = Long.parseLong(item);
    } catch (Exception e) {
      this.itemLong = -1L;
    }
    type = String.class;
  }

  /**
   * Gets a Long Value from a Row.
   *
   * @return Value as a long.
   */
  public long getLong() {
    return itemLong;
  }

  /**
   * Returns a String value from the database.
   *
   * @return Value as a String
   */
  public String getString() {
    return itemString;
  }

  /**
   * Returns the Key from the Database.
   * @return Key String.
   */
  public String getKey() {
    return key;
  }
}
