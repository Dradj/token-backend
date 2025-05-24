package com.zraj.tokenbackend;

import java.nio.charset.Charset;
import java.util.BitSet;

public class RFC5987 {
    private static final BitSet RFC5987_VALID = new BitSet(256);

    static {
        // Разрешенные символы по RFC 5987
        for (int i = '0'; i <= '9'; i++) RFC5987_VALID.set(i);
        for (int i = 'A'; i <= 'Z'; i++) RFC5987_VALID.set(i);
        for (int i = 'a'; i <= 'z'; i++) RFC5987_VALID.set(i);
        RFC5987_VALID.set('!');
        RFC5987_VALID.set('#');
        RFC5987_VALID.set('$');
        RFC5987_VALID.set('&');
        RFC5987_VALID.set('+');
        RFC5987_VALID.set('-');
        RFC5987_VALID.set('.');
        RFC5987_VALID.set('^');
        RFC5987_VALID.set('_');
        RFC5987_VALID.set('`');
        RFC5987_VALID.set('|');
        RFC5987_VALID.set('~');
    }

    public static String encode(String value, Charset charset) {
        final byte[] bytes = value.getBytes(charset);
        final StringBuilder sb = new StringBuilder(bytes.length << 1);
        for (byte b : bytes) {
            int c = b & 0xFF;
            if (RFC5987_VALID.get(c)) {
                sb.append((char) c);
            } else {
                sb.append('%');
                sb.append(Character.toUpperCase(Character.forDigit((c >> 4) & 0xF, 16)));
                sb.append(Character.toUpperCase(Character.forDigit(c & 0xF, 16)));
            }
        }
        return sb.toString();
    }
}
