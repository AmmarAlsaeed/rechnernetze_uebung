package de.ipvs.RNI.uebung1;

public interface IBitManipulation {
    /**
    * Negiertes-Oder
    *
    * @param  x  Erste Zahl
    * @param  y  Zweite Zahl
    * @return
    */
    int bitNor(int x, int y);

    /**
    * Ausschließendes-Oder
    *
    * @param  x  Erste Zahl
    * @param  y  Zweite Zahl
    * @return
    */
    int bitXor(int x, int y);

    /**
    * Extrahiere das n-te Byte eines Integers (32bit)
    *
    * @param  x  Integerzahl
    * @param  n  0 bis 3
    * @return
    */
    byte getByte(int x, short n);

    /**
    * Umgekehrte Byte-Reihenfolge.
    *
    * @param  x  Integerzahl
    * @return
    */
    int reverseBytes(int x);

    /**
    * Rückgabe ist das n-te Bit eines Bytes
    *
    * @param  B  Byte
    * @param  position Bit an position
    * @return
    */
    int getBit(byte B, int position);

    /**
    * Bestimme, ob die Addition von zwei Integerzahlen zu einem Overflow führt.
    *
    * @param  x  Erste Zahl
    * @param  y  Zweite Zahl
    * @return  true, wenn Overflow
    */
    boolean addOverflowCheck(int x, int y);

    /**
     * Wandelt ein Byte-Array in ein Char-Array, das einen MLT3 Leitungscode darstellt, um.
     *
     * Dabei gehen sie wie folgt vor:
     * Bestimmen Sie den Spannungspegel für jedes Bit in jedem Byte.
     * Dabei gibt es zwischen drei Pegel zu unterscheiden, nämlich 'H' (high), '0' (zero), 'L' (low).
     * Für jedes Bit wird einer dieser Pegelsymbole als Char-Zeichen in einem Char-Array gespeichert
     * und am Ende zurückgegeben.
     *
     * Ein Beispiel finden Sie im Test "TestBitManipulation.testMLT3Encoder"
     *
     * Hinweis: Der Pegel ist am Anfang auf '0'.
     *
     *
     * @param byteStream
     * @return Char-Array, das die MLT3 Darstellung eines Byte Arrays durch die Zeichen 'H','0', und 'L' codiert.
     */
    char[] MLT3Encoder(byte[] byteStream);

    /**
     * Wandelt einen MLT3 Leitungscode in ein Byte-Array zurück.
     *
     * Der Leitungscode ist ein Char-Array, dass nur aus drei Zeichen bestehen darf  'H' (high), '0' (zero), 'L' (low).
     * Diese drei Zeichen beschreiben den Spannungspegel, der durch ein MLT3 erzeugt wurde.
     * Jedes Zeichen (Symbol) entspricht einem Bit aus einem Byte. Nach acht Symbole ist ein Byte abgeschlossen.
     * Mit dem nächsten Symbol fängt ein neues Byte an.
     *
     * Ein Beispiel finden Sie im Test "TestBitManipulation.testMLT3Decoder"
     *
     * Hinweis: Gehen Sie davon aus, dass am Anfang der Pegel auf '0' ist.
     *
     * @param inputStream
     * @return
     */
    byte[] MLT3Decoder(char[] inputStream);


    //  Für die nächste Aufgabe benutzen Sie folgende Tabelle.
    //      |     Data      | 4B5B Code       |
    //      |  Hex | Binary |    Binary | Dez |
    //      |------|--------|-----------|---- |
    //      |    0 |   0000 |     11110 | 30  |
    //      |    1 |   0001 |     01001 | 9   |
    //      |    2 |   0010 |     10100 | 20  |
    //      |    3 |   0011 |     10101 | 21  |
    //      |    4 |   0100 |     01010 | 10  |
    //      |    5 |   0101 |     01011 | 11  |
    //      |    6 |   0110 |     01110 | 14  |
    //      |    7 |   0111 |     01111 | 15  |
    //      |    8 |   1000 |     10010 | 18  |
    //      |    9 |   1001 |     10011 | 19  |
    //      |    A |   1010 |     10110 | 22  |
    //      |    B |   1011 |     10111 | 23  |
    //      |    C |   1100 |     11010 | 26  |
    //      |    D |   1101 |     11011 | 27  |
    //      |    E |   1110 |     11100 | 28  |
    //      |    F |   1111 |     11101 | 29  |

    /**
     * Diese Funktion wandelt ein Byte-Array in ein Boolean-Array um.
     *
     * Das Boolean-Array enthält die bitweise Darstellung des 4B5B-Codes eines Byte-Arrays.
     * Jedes Byte wird in seine zwei Semi-Oktetts aufgeteilt und jedes Semi-Oktett wird
     * dementsprechend in seiner 5B-Darstellung bitweise in das Boolean-Array eingefügt.
     *
     * Dabei soll 'false' für das Bit 0 und 'true' für das Bit 1 verwendet werde.
     *
     * Ein Beispiel finden Sie im Test "TestBitManipulation.test4B5Bencode"
     *
     *    Bitte beachten Sie, wie Arrays und Bits aus Bytes eingelesen werden und wie diese
     *    dargestellt werden.
     *
     * @param message  Byte Array
     * @return
     */
    boolean[] e4B5BEncoder(byte[] message);

    /**
     * Diese Funktion wandelt ein Boolean-Array in ein Byte-Array zurück.
     *
     * Dabei stellt das Boolean-Array die 4B5B Codierung des Bytes-Arrays dar.
     * Jeder bool'sche Wert im Array steht für ein Bit. Fünf solcher Bits repräsentieren
     * ein 5B-Symbol. Ein 5B-Symbol steht für ein Semi-Oktetts aus dem Byte. Zwei Semi-Oktetts bilden ein Byte.
     * Nachdem ein Byte zusammengesetzt wird, beginnen die Bits eines neuen Bytes im Boolean Array.
     * Alle Bytes werden der Reihenfolge nach als Byte-Array zurückgegeben.
     * Diese Funktion ist somit die Rücktransformation von e4B5BEncoder.
     *
     * Ein Beispiel finden Sie im Test "TestBitManipulation.test4B5Bdecode"
     *
     * @param inputStream
     * @return
     */
    byte[] d4B5BDecoder(boolean[] inputStream);
}
