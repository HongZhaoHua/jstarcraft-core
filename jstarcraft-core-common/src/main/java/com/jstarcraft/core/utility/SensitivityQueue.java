package com.jstarcraft.core.utility;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 敏感度队列
 * 
 * @author Bridy
 *
 * @param <E>
 */
public class SensitivityQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {

    private class SensitivityIterator implements Iterator<E> {

        private final Object[] elements;
        private int cursor;
        private Object current = null;

        SensitivityIterator(Object[] elements) {
            this.elements = elements;
        }

        @Override
        public boolean hasNext() {
            return cursor < elements.length;
        }

        @Override
        public E next() {
            if (cursor >= elements.length) {
                throw new NoSuchElementException();
            }
            current = elements[cursor++];
            return (E) current;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            try {
                lock.lock();
                for (Iterator<?> iterator = queue.iterator(); iterator.hasNext();) {
                    if (iterator.next() == current) {
                        iterator.remove();
                        break;
                    }
                }
                current = null;
            } finally {
                lock.unlock();
            }
        }

    }

    /** 锁 */
    private transient final ReentrantLock lock = new ReentrantLock();
    /** 信号量 */
    private transient final Condition condition = lock.newCondition();

    /** 修正时间(毫秒) */
    private final long sensitivity;
    /** 延迟队列 */
    private final PriorityQueue<E> queue = new PriorityQueue<E>();

    public SensitivityQueue(long sensitivity) {
        this.sensitivity = sensitivity;
    }

    @Override
    public E take() throws InterruptedException {
        try {
            lock.lockInterruptibly();
            for (;;) {
                E current = queue.peek();
                if (current == null) {
                    condition.await();
                } else {
                    long delay = current.getDelay(TimeUnit.MILLISECONDS);
                    if (delay > 0) {
                        boolean interrupt = condition.await(delay < sensitivity ? delay : sensitivity, TimeUnit.MILLISECONDS);
                        if (!interrupt) {
                            // 线程等待异常
                            continue;
                        }
                    } else {
                        E element = queue.poll();
                        assert element != null;
                        if (queue.size() != 0) {
                            condition.signalAll();
                        }
                        return element;
                    }
                }
            }
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    @Override
    public E poll(long expire, TimeUnit unit) throws InterruptedException {
        try {
            lock.lockInterruptibly();
            expire = unit.toMillis(expire);
            for (;;) {
                E current = queue.peek();
                if (current == null) {
                    if (expire <= 0)
                        return null;
                    else {
                        long now = System.currentTimeMillis();
                        boolean interrupt = condition.await(sensitivity > expire ? expire : sensitivity, TimeUnit.MILLISECONDS);
                        expire -= System.currentTimeMillis() - now;
                        if (!interrupt) {
                            // 线程等待异常
                            continue;
                        }
                    }
                } else {
                    long delay = current.getDelay(TimeUnit.MILLISECONDS);
                    if (delay > 0) {
                        if (expire <= 0) {
                            return null;
                        }
                        if (delay > expire) {
                            delay = expire;
                        }
                        if (delay > sensitivity) {
                            delay = sensitivity;
                        }
                        long now = System.currentTimeMillis();
                        boolean interrupt = condition.await(delay, TimeUnit.MILLISECONDS);
                        expire -= System.currentTimeMillis() - now;
                        if (!interrupt) {
                            // 线程等待异常
                            return null;
                        }
                    } else {
                        E element = queue.poll();
                        assert element != null;
                        if (queue.size() != 0) {
                            condition.signalAll();
                        }
                        return element;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean add(E element) {
        try {
            lock.lock();
            E current = queue.peek();
            queue.offer(element);
            if (current == null || element.compareTo(current) < 0) {
                condition.signalAll();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(E element) {
        add(element);
    }

    @Override
    public boolean offer(E element) {
        return add(element);
    }

    @Override
    public boolean offer(E element, long expire, TimeUnit unit) {
        return add(element);
    }

    @Override
    public E poll() {
        try {
            lock.lock();
            E current = queue.peek();
            if (current == null || current.getDelay(TimeUnit.NANOSECONDS) > 0)
                return null;
            else {
                E element = queue.poll();
                assert element != null;
                if (queue.size() != 0) {
                    condition.signalAll();
                }
                return element;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E peek() {
        try {
            lock.lock();
            return queue.peek();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        try {
            lock.lock();
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> collection) {
        return drainTo(collection, Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super E> collection, int size) {
        if (collection == null) {
            throw new NullPointerException();
        }
        if (collection == this) {
            throw new IllegalArgumentException();
        }
        if (size <= 0) {
            return 0;
        }
        try {
            lock.lock();
            int index = 0;
            while (index < size) {
                E current = queue.peek();
                if (current == null || current.getDelay(TimeUnit.NANOSECONDS) > 0) {
                    break;
                }
                collection.add(queue.poll());
                ++index;
            }
            if (index > 0) {
                condition.signalAll();
            }
            return index;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            queue.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object[] toArray() {
        try {
            lock.lock();
            return queue.toArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(T[] array) {
        try {
            lock.lock();
            return queue.toArray(array);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object element) {
        try {
            lock.lock();
            return queue.remove(element);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new SensitivityIterator(toArray());
    }

}
