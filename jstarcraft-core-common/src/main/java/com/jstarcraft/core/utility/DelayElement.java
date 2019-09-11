package com.jstarcraft.core.utility;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时元素
 * 
 * @author Birdy
 */
public class DelayElement<T> implements Delayed {

    /** 元素内容 */
    private final T content;
    /** 元素到期 */
    private final Date expire;

    public DelayElement(T content, Instant expire) {
        this(content, Date.from(expire));
    }

    public DelayElement(T content, Date expire) {
        this.content = content;
        this.expire = expire;
    }

    public T getContent() {
        return content;
    }

    public Date getExpire() {
        return expire;
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        long now = System.currentTimeMillis();
        long expire = this.expire.getTime() - now;
        switch (timeUnit) {
        case MILLISECONDS:
            return expire;
        case SECONDS:
            return TimeUnit.MILLISECONDS.toSeconds(expire);
        case MINUTES:
            return TimeUnit.MILLISECONDS.toMinutes(expire);
        case HOURS:
            return TimeUnit.MILLISECONDS.toHours(expire);
        case DAYS:
            return TimeUnit.MILLISECONDS.toDays(expire);
        case MICROSECONDS:
            return TimeUnit.MILLISECONDS.toMicros(expire);
        case NANOSECONDS:
            return TimeUnit.MILLISECONDS.toNanos(expire);
        }
        return expire;
    }

    @Override
    public int compareTo(Delayed that) {
        long thisDelay = this.getDelay(TimeUnit.MILLISECONDS);
        long thatDelay = that.getDelay(TimeUnit.MILLISECONDS);
        if (thisDelay < thatDelay) {
            return -1;
        }
        if (thisDelay > thatDelay) {
            return 1;
        }
        // 时间判断无法区分时，执行如下判断(用于维持 compareTo 的使用约束)
        if (this.equals(that)) {
            return 0;
        } else {
            return this.hashCode() - that.hashCode();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + ((content == null) ? 0 : content.hashCode());
        hash = prime * hash + ((expire == null) ? 0 : expire.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        DelayElement other = (DelayElement) object;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (expire == null) {
            if (other.expire != null)
                return false;
        } else if (!expire.equals(other.expire))
            return false;
        return true;
    }

}