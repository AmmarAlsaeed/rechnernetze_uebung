package de.ipvs.RNI.uebung1;

import com.sun.jdi.request.BreakpointRequest;

import java.util.ArrayList;
import java.util.List;

public class BitManipulation implements IBitManipulation {
  /**
   * Negiertes-Oder
   *
   * @param x Erste Zahl
   * @param y Zweite Zahl
   * @return
   */
  @Override
  public int bitNor(int x, int y) {
    return ~(x | y);
  }

  /**
   * Ausschließendes-Oder
   *
   * @param x Erste Zahl
   * @param y Zweite Zahl
   * @return
   */
  @Override
  public int bitXor(int x, int y) {
    return x ^ y;
  }

  /**
   * Extrahiere das n-te Byte eines Integers (32bit)
   *
   * @param x Integerzahl
   * @param n 0 bis 3
   * @return
   */
  @Override
  public byte getByte(int x, short n) {
    return (byte)((x >> (8 * n)) & 0xff);
  }

  /**
   * Umgekehrte Byte-Reihenfolge.
   *
   * @param x Integerzahl
   * @return
   */
  @Override
  public int reverseBytes(int x) {
    return Integer.reverseBytes(x);
  }

  /**
   * Rückgabe ist das n-te Bit eines Bytes
   *
   * @param B        Byte
   * @param position Bit an position
   * @return
   */
  @Override
  public int getBit(byte B, int position) {
    return (B >> position) & 0x1;
  }

  /**
   * Bestimme, ob die Addition von zwei Integerzahlen zu einem Overflow führt.
   *
   * @param x Erste Zahl
   * @param y Zweite Zahl
   * @return true, wenn Overflow
   */
  @Override
  public boolean addOverflowCheck(int x, int y) {
    long result = (long)x + (long)y;
    return result < Integer.MIN_VALUE || result > Integer.MAX_VALUE;
  }

  /**
   * Wandelt ein Byte-Array in ein Char-Array, das einen MLT3 Leitungscode darstellt, um.
   * <p>
   * Dabei gehen sie wie folgt vor:
   * Bestimmen Sie den Spannungspegel für jedes Bit in jedem Byte.
   * Dabei gibt es zwischen drei Pegel zu unterscheiden, nämlich 'H' (high), '0' (zero), 'L' (low).
   * Für jedes Bit wird einer dieser Pegelsymbole als Char-Zeichen in einem Char-Array gespeichert
   * und am Ende zurückgegeben.
   * <p>
   * Ein Beispiel finden Sie im Test "TestBitManipulation.testMLT3Encoder"
   * <p>
   * Hinweis: Der Pegel ist am Anfang auf '0'.
   *
   * @param byteStream
   * @return Char-Array, das die MLT3 Darstellung eines Byte Arrays durch die Zeichen 'H','0', und 'L' codiert.
   */
  @Override
  public char[] MLT3Encoder(byte[] byteStream) {
    int state = 0; //state machine for encoding
    StringBuilder result = new StringBuilder();

    for (byte b : byteStream) {
      for (int i = 7; i >= 0; i--) { //iterate over bits
        if (getBit(b, i) > 0) {
          state = (state + 1) % 4; //advance to next state if bit is 1
        }

        switch (state) { //append symbol based on state
          case 0:
          case 2:
            result.append('0');
            break;
          case 1:
            result.append('H');
            break;
          case 3:
            result.append('L');
            break;
        }
      }
    }

    return result.toString().toCharArray();
  }

  /**
   * Wandelt einen MLT3 Leitungscode in ein Byte-Array zurück.
   * <p>
   * Der Leitungscode ist ein Char-Array, dass nur aus drei Zeichen bestehen darf  'H' (high), '0' (zero), 'L' (low).
   * Diese drei Zeichen beschreiben den Spannungspegel, der durch ein MLT3 erzeugt wurde.
   * Jedes Zeichen (Symbol) entspricht einem Bit aus einem Byte. Nach acht Symbole ist ein Byte abgeschlossen.
   * Mit dem nächsten Symbol fängt ein neues Byte an.
   * <p>
   * Ein Beispiel finden Sie im Test "TestBitManipulation.testMLT3Decoder"
   * <p>
   * Hinweis: Gehen Sie davon aus, dass am Anfang der Pegel auf '0' ist.
   *
   * @param inputStream
   * @return
   */
  @Override
  public byte[] MLT3Decoder(char[] inputStream) {
    byte[] result = new byte[inputStream.length / 8];
    char previous = '0';

    for (int i = 0; i < result.length; i++) {
      byte b = 0;

      for (int j = 0; j < 8; j++) { //iterate over bits
        b <<= 1; //shift bits
        char current = inputStream[8 * i + j];
        if (current != previous) { //add '1' bit wherever the encoded symbol changes
          b |= 0x1;
          previous = current;
        }
      }

      result[i] = b;
    }

    return result;
  }

  /**
   * Diese Funktion wandelt ein Byte-Array in ein Boolean-Array um.
   * <p>
   * Das Boolean-Array enthält die bitweise Darstellung des 4B5B-Codes eines Byte-Arrays.
   * Jedes Byte wird in seine zwei Semi-Oktetts aufgeteilt und jedes Semi-Oktett wird
   * dementsprechend in seiner 5B-Darstellung bitweise in das Boolean-Array eingefügt.
   * <p>
   * Dabei soll 'false' für das Bit 0 und 'true' für das Bit 1 verwendet werde.
   * <p>
   * Ein Beispiel finden Sie im Test "TestBitManipulation.test4B5Bencode"
   * <p>
   * Bitte beachten Sie, wie Arrays und Bits aus Bytes eingelesen werden und wie diese
   * dargestellt werden.
   *
   * @param message Byte Array
   * @return
   */
  @Override
  public boolean[] e4B5BEncoder(byte[] message) {
    final boolean[][] mapping = new boolean[][] { //lookup table for 4B5B encoding
        new boolean[] { true, true, true, true, false },
        new boolean[] { false, true, false, false, true },
        new boolean[] { true, false, true, false, false },
        new boolean[] { true, false, true, false, true },
        new boolean[] { false, true, false, true, false },
        new boolean[] { false, true, false, true, true },
        new boolean[] { false, true, true, true, false },
        new boolean[] { false, true, true, true, true },
        new boolean[] { true, false, false, true, false },
        new boolean[] { true, false, false, true, true },
        new boolean[] { true, false, true, true, false },
        new boolean[] { true, false, true, true, true },
        new boolean[] { true, true, false, true, false },
        new boolean[] { true, true, false, true, true },
        new boolean[] { true, true, true, false, false },
        new boolean[] { true, true, true, false, true }
    };

    boolean[] result = new boolean[10 * message.length]; //10 encoded bits per input byte

    for (int i = 0; i < message.length; i++) { //iterate over input bytes
      byte current = message[i];
      System.arraycopy(mapping[current >> 4], 0, result, i * 10, 5); //insert encoding of first half byte
      System.arraycopy(mapping[current & 0xf], 0, result, i * 10 + 5, 5); //insert encoding of second half byte
    }

    return result;
  }

  /**
   * Helper method for conversion of boolean sequences to numbers
   * @param booleans boolean sequence
   * @param index starting index in sequence
   * @return number from binary interpretation of 5 booleans following the starting index
   */
  private int bitsFromBooleans(boolean[] booleans, int index) {
    int result = 0;

    for (int i = 0; i < 5; i++) {
      result <<= 1;
      if (booleans[index + i]) {
        result |= 0x1;
      }
    }

    return result;
  }

  /**
   * Diese Funktion wandelt ein Boolean-Array in ein Byte-Array zurück.
   * <p>
   * Dabei stellt das Boolean-Array die 4B5B Codierung des Bytes-Arrays dar.
   * Jeder bool'sche Wert im Array steht für ein Bit. Fünf solcher Bits repräsentieren
   * ein 5B-Symbol. Ein 5B-Symbol steht für ein Semi-Oktetts aus dem Byte. Zwei Semi-Oktetts bilden ein Byte.
   * Nachdem ein Byte zusammengesetzt wird, beginnen die Bits eines neuen Bytes im Boolean Array.
   * Alle Bytes werden der Reihenfolge nach als Byte-Array zurückgegeben.
   * Diese Funktion ist somit die Rücktransformation von e4B5BEncoder.
   * <p>
   * Ein Beispiel finden Sie im Test "TestBitManipulation.test4B5Bdecode"
   *
   * @param inputStream
   * @return
   */
  @Override
  public byte[] d4B5BDecoder(boolean[] inputStream) {
    final byte[] mapping = new byte[] { //lookup table for 4B5B decoding, invalid codes map to 0
      0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
      0x0, 0x1, 0x4, 0x5, 0x0, 0x0, 0x6, 0x7,
      0x0, 0x0, 0x8, 0x9, 0x2, 0x3, 0xA, 0xB,
      0x0, 0x0, 0xC, 0xD, 0xE, 0xF, 0x0, 0x0
    };

    byte[] result = new byte[inputStream.length / 10]; //10 encoded bits per output byte

    for (int i = 0; i < result.length; i++) { //iterate over output bytes
      byte b = 0;

      b |= mapping[bitsFromBooleans(inputStream, 10 * i)]; //decode first half byte
      b <<= 4; //shift first half byte into position
      b |= mapping[bitsFromBooleans(inputStream, 10 * i + 5)]; //decode second half byte

      result[i] = b;
    }

    return result;
  }
}
