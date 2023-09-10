package com.erliapp.utilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/** Java Properties Extension. */
public class PropertiesEx extends Properties {

  private static final char[] hexDigit =
      new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  /**
   * Writes comments.
   *
   * @param bw Buffered Writer to write
   * @param comments Comments
   * @throws IOException Any IO operation that fails
   */
  private static void writeComments(BufferedWriter bw, String comments) throws IOException {
    bw.write("#");
    int len = comments.length();
    int current = 0;
    int last = 0;

    for (char[] uu = new char[] {'\\', 'u', '\u0000', '\u0000', '\u0000', '\u0000'};
        current < len;
        ++current) {
      char c = comments.charAt(current);
      if (c > 255 || c == '\n' || c == '\r') {
        if (last != current) {
          bw.write(comments.substring(last, current));
        }

        if (c > 255) {
          uu[2] = toHex(c >> 12 & 15);
          uu[3] = toHex(c >> 8 & 15);
          uu[4] = toHex(c >> 4 & 15);
          uu[5] = toHex(c & 15);
          bw.write(new String(uu));
        } else {
          bw.newLine();
          if (c == '\r' && current != len - 1 && comments.charAt(current + 1) == '\n') {
            ++current;
          }

          if (current == len - 1
              || comments.charAt(current + 1) != '#' && comments.charAt(current + 1) != '!') {
            bw.write("#");
          }
        }

        last = current + 1;
      }
    }

    if (last != current) {
      bw.write(comments.substring(last, current));
    }

    bw.newLine();
  }

  /**
   * Converts an integer to Hex.
   *
   * @param nibble Nibble to convert
   * @return Hex character.
   */
  private static char toHex(int nibble) {
    return hexDigit[nibble & 15];
  }

  /**
   * Saves data as a String.
   *
   * @param toSave String to save
   * @param escapeSpace Escape code for Space
   * @param escapeUnicode Escape code for Unicode
   * @return Converted String
   */
  private String saveConvert(String toSave, boolean escapeSpace, boolean escapeUnicode) {
    int len = toSave.length();
    int bufLen = len * 2;
    if (bufLen < 0) {
      bufLen = 2147483647;
    }

    StringBuilder outBuffer = new StringBuilder(bufLen);

    for (int x = 0; x < len; ++x) {
      char saveChar = toSave.charAt(x);
      if (saveChar > '=' && saveChar < 127) {
        if (saveChar == '\\') {
          outBuffer.append('\\');
          outBuffer.append('\\');
        } else {
          outBuffer.append(saveChar);
        }
      } else {
        switch (saveChar) {
          case '\t':
            outBuffer.append('\\');
            outBuffer.append('t');
            continue;
          case '\n':
            outBuffer.append('\\');
            outBuffer.append('n');
            continue;
          case '\f':
            outBuffer.append('\\');
            outBuffer.append('f');
            continue;
          case '\r':
            outBuffer.append('\\');
            outBuffer.append('r');
            continue;
          case ' ':
            if (x == 0 || escapeSpace) {
              outBuffer.append('\\');
            }

            outBuffer.append(' ');
            continue;
          case '!':
          case '#':
          case '=':
            outBuffer.append('\\');
            outBuffer.append(saveChar);
            continue;
          default:
            break;
        }

        if ((saveChar < ' ' || saveChar > '~') & escapeUnicode) {
          outBuffer.append('\\');
          outBuffer.append('u');
          outBuffer.append(toHex(saveChar >> 12 & 15));
          outBuffer.append(toHex(saveChar >> 8 & 15));
          outBuffer.append(toHex(saveChar >> 4 & 15));
          outBuffer.append(toHex(saveChar & 15));
        } else {
          outBuffer.append(saveChar);
        }
      }
    }

    return outBuffer.toString();
  }

  /**
   * Stores the data to a file.
   *
   * @param out Output Stream to store
   * @param comments Any comments to store
   * @throws IOException If it fails
   */
  public void store(OutputStream out, String comments) throws IOException {
    this.store1(new BufferedWriter(new OutputStreamWriter(out, "8859_1")), comments);
  }

  /**
   * Internal Store data.
   *
   * @param bw BufferedWriter to store to
   * @param comments Comments to add
   * @throws IOException Any exception that happens
   */
  private void store1(BufferedWriter bw, String comments) throws IOException {
    if (comments != null) {
      writeComments(bw, comments);
    }

    bw.write("#" + (new Date()));
    bw.newLine();
    synchronized (this) {
      Iterator var5 = this.entrySet().iterator();

      while (var5.hasNext()) {
        Map.Entry<Object, Object> e = (Map.Entry) var5.next();
        String key = (String) e.getKey();
        String val = (String) e.getValue();
        bw.write(key + "=" + val);
        bw.newLine();
      }
    }

    bw.flush();
    bw.close();
  }
}
