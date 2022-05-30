package com.jstarcraft.core.storage.berkeley.memorandum;

import java.time.Instant;

import com.sleepycat.je.Environment;

/**
 * 备忘录
 * 
 * @author Birdy
 *
 */
public interface Memorandum {

    void checkIn(Environment environment, Instant now);

    void checkOut(Instant from, Instant to);

    void clean(Instant expire);

}
