package com.jstarcraft.core.event;

import com.jstarcraft.core.utility.StringUtility;

public class EventPattern {

    private char fromSeparator;

    private char toSeparator;

    private char fromOnly;

    private char toOnly;

    private char fromSome;

    private char toSome;

    public EventPattern(char fromSeparator, char toSeparator, char fromOnly, char toOnly, char fromSome, char toSome) {
        this.fromSeparator = fromSeparator;
        this.toSeparator = toSeparator;
        this.fromOnly = fromOnly;
        this.toOnly = toOnly;
        this.fromSome = fromSome;
        this.toSome = toSome;
    }

    public String convert(CharSequence address) {
        int size = address.length();
        if (size == 0) {
            return StringUtility.EMPTY;
        }
        StringBuilder buffer = new StringBuilder(size);
        for (int index = 0; index < size; index++) {
            char character = address.charAt(index);
            if (character == fromSeparator) {
                character = toSeparator;
                buffer.append(character);
                continue;
            }
            if (character == fromOnly) {
                character = toOnly;
                buffer.append(character);
                continue;
            }
            if (character == fromSome) {
                character = toSome;
                buffer.append(character);
                continue;
            }
            buffer.append(character);
        }
        return buffer.toString();
    }

    public char getFromSeparator() {
        return fromSeparator;
    }

    public char getToSeparator() {
        return toSeparator;
    }

    public char getFromOnly() {
        return fromOnly;
    }

    public char getToOnly() {
        return toOnly;
    }

    public char getFromSome() {
        return fromSome;
    }

    public char getToSome() {
        return toSome;
    }

}
