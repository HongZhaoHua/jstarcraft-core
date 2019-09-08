package com.jstarcraft.core.utility;

import static java.lang.Float.floatToRawIntBits;
import static java.lang.Float.intBitsToFloat;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicFloatArray implements java.io.Serializable {
    private static final long serialVersionUID = 0L;

    // Making this non-final is the lesser evil according to Effective
    // Java 2nd Edition Item 76: Write readObject methods defensively.
    private transient AtomicIntegerArray values;

    /**
     * Creates a new {@code AtomicFloatArray} of the given length, with all elements
     * initially zero.
     *
     * @param length the length of the array
     */
    public AtomicFloatArray(int length) {
        this.values = new AtomicIntegerArray(length);
    }

    /**
     * Creates a new {@code AtomicFloatArray} with the same length as, and all
     * elements copied from, the given array.
     *
     * @param array the array to copy elements from
     * @throws NullPointerException if array is null
     */
    public AtomicFloatArray(float[] array) {
        final int len = array.length;
        int[] integerArray = new int[len];
        for (int i = 0; i < len; i++) {
            integerArray[i] = floatToRawIntBits(array[i]);
        }
        this.values = new AtomicIntegerArray(integerArray);
    }

    /**
     * Returns the length of the array.
     *
     * @return the length of the array
     */
    public final int length() {
        return values.length();
    }

    /**
     * Gets the current value at position {@code i}.
     *
     * @param i the index
     * @return the current value
     */
    public final float get(int i) {
        return intBitsToFloat(values.get(i));
    }

    /**
     * Sets the element at position {@code i} to the given value.
     *
     * @param i        the index
     * @param newValue the new value
     */
    public final void set(int i, float newValue) {
        int next = floatToRawIntBits(newValue);
        values.set(i, next);
    }

    /**
     * Eventually sets the element at position {@code i} to the given value.
     *
     * @param i        the index
     * @param newValue the new value
     */
    public final void lazySet(int i, float newValue) {
        set(i, newValue);
        // TODO(user): replace with code below when jdk5 support is dropped.
        // int next = floatToRawIntBits(newValue);
        // ints.lazySet(i, next);
    }

    /**
     * Atomically sets the element at position {@code i} to the given value and
     * returns the old value.
     *
     * @param i        the index
     * @param newValue the new value
     * @return the previous value
     */
    public final float getAndSet(int i, float newValue) {
        int next = floatToRawIntBits(newValue);
        return intBitsToFloat(values.getAndSet(i, next));
    }

    /**
     * Atomically sets the element at position {@code i} to the given updated value
     * if the current value is <a href="#bitEquals">bitwise equal</a> to the
     * expected value.
     *
     * @param i      the index
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that the actual value was
     *         not equal to the expected value.
     */
    public final boolean compareAndSet(int i, float expect, float update) {
        return values.compareAndSet(i, floatToRawIntBits(expect), floatToRawIntBits(update));
    }

    /**
     * Atomically sets the element at position {@code i} to the given updated value
     * if the current value is <a href="#bitEquals">bitwise equal</a> to the
     * expected value.
     *
     * <p>
     * May <a href=
     * "http://download.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/package-summary.html#Spurious">
     * fail spuriously</a> and does not provide ordering guarantees, so is only
     * rarely an appropriate alternative to {@code compareAndSet}.
     *
     * @param i      the index
     * @param expect the expected value
     * @param update the new value
     * @return true if successful
     */
    public final boolean weakCompareAndSet(int i, float expect, float update) {
        return values.weakCompareAndSet(i, floatToRawIntBits(expect), floatToRawIntBits(update));
    }

    /**
     * Atomically adds the given value to the element at index {@code i}.
     *
     * @param i     the index
     * @param delta the value to add
     * @return the previous value
     */
    public final float getAndAdd(int i, float delta) {
        while (true) {
            int current = values.get(i);
            float currentVal = intBitsToFloat(current);
            float nextVal = currentVal + delta;
            int next = floatToRawIntBits(nextVal);
            if (values.compareAndSet(i, current, next)) {
                return currentVal;
            }
        }
    }

    /**
     * Atomically adds the given value to the element at index {@code i}.
     *
     * @param i     the index
     * @param delta the value to add
     * @return the updated value
     */
    public float addAndGet(int i, float delta) {
        while (true) {
            int current = values.get(i);
            float currentVal = intBitsToFloat(current);
            float nextVal = currentVal + delta;
            int next = floatToRawIntBits(nextVal);
            if (values.compareAndSet(i, current, next)) {
                return nextVal;
            }
        }
    }

    /**
     * Returns the String representation of the current values of array.
     * 
     * @return the String representation of the current values of array
     */
    public String toString() {
        int iMax = length() - 1;
        if (iMax == -1) {
            return "[]";
        }

        // Float.toString(Math.PI).length() == 17
        StringBuilder b = new StringBuilder((17 + 2) * (iMax + 1));
        b.append('[');
        for (int i = 0;; i++) {
            b.append(intBitsToFloat(values.get(i)));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(',').append(' ');
        }
    }

    /**
     * Saves the state to a stream (that is, serializes it).
     *
     * @serialData The length of the array is emitted (int), followed by all of its
     *             elements (each a {@code float}) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        s.defaultWriteObject();

        // Write out array length
        int length = length();
        s.writeInt(length);

        // Write out all elements in the proper order.
        for (int i = 0; i < length; i++) {
            s.writeFloat(get(i));
        }
    }

    /**
     * Reconstitutes the instance from a stream (that is, deserializes it).
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();

        // Read in array length and allocate array
        int length = s.readInt();
        this.values = new AtomicIntegerArray(length);

        // Read in all elements in the proper order.
        for (int i = 0; i < length; i++) {
            set(i, s.readFloat());
        }
    }
}
