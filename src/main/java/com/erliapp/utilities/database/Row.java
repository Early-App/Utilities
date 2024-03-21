package com.erliapp.utilities.database;

import java.util.UUID;

/** Handles a Row of data within a Database. */
public class Row<T extends Object> {

  private final Class<?>[] allowedTypes = new Class[]{
          String.class,
          Double.class,
          Long.class,
          UUID.class
  };

  private final Class<?> type;

  private T item;

  /**
   * Row Constructor.
   *
   * @param iClass Class Type.
   * @param item Row data.
   */
  public Row(Class<T> iClass, T item) {
    boolean found = false;
    for (Class<?> c : allowedTypes) {
      if (c.getName().equals(iClass.getName()) && c.getPackageName().equals(iClass.getPackageName())) {
        found = true;
      }
    }
    if (found) {
      this.type = iClass;
      this.item = item;
    } else {
      throw new RuntimeException("Class " + iClass.getName() + " is not a valid type.");
    }
  }


  public T getItem() {
    return this.item;
  }

  /**
   * Gets a Long Value from a Row.
   *
   * @return Value as a long.
   */
  public long getLong() {

    if (item instanceof Long) {
      return (long) item;
    } else {
      String s = getString();

      try {
        return Long.parseLong(s);
      } catch (Exception e) {
        return -1L;
      }
    }
  }

  public Double getDouble() {
    if (item instanceof Double) {
      return (double) item;
    } else {
      String s = getString();

      try {
        return Double.parseDouble(s);
      } catch (Exception e) {
        return -1.0;
      }
    }
  }

  public UUID getUUID() {
    if (item instanceof UUID) {
      return (UUID) item;
    } else {
      String s = getString();

      try {
        return UUID.fromString(s);
      } catch (IllegalArgumentException e) {
        return null;
      }
    }
  }

  /**
   * Returns a String value from the database.
   *
   * @return Value as a String
   */
  public String getString() {
    return item.toString();
  }

  public Class<?> getType() {
    return type;
  }
}
