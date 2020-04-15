package com.jstarcraft.core.storage.berkeley.migration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.storage.berkeley.BerkeleyAccessor;
import com.jstarcraft.core.storage.berkeley.migration.older.Information;
import com.jstarcraft.core.storage.berkeley.migration.older.Item;
import com.jstarcraft.core.storage.berkeley.migration.older.Player;
import com.jstarcraft.core.storage.berkeley.migration.persistent.Enumerate;
import com.jstarcraft.core.storage.berkeley.migration.persistent.Persist;
import com.jstarcraft.core.utility.StringUtility;

class Batch extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Batch.class);

    private final BerkeleyAccessor accessor;

    private volatile long total = 0;
    private final CyclicBarrier barrier;
    private final int size;

    public Batch(CyclicBarrier barrier, int size, BerkeleyAccessor accessor) {
        this.accessor = accessor;
        this.barrier = barrier;
        this.size = size;
    }

    public final void run() {
        try {
            barrier.await();
            long start = System.currentTimeMillis();
            try {
                this.execute();
            } catch (Exception exception) {
                logger.error(StringUtility.EMPTY, exception);
            }
            long end = System.currentTimeMillis();
            total = end - start;
            barrier.await();
        } catch (Exception exception) {
            logger.error(StringUtility.EMPTY, exception);
        }
    }

    private void execute() {
        final Map<Enumerate, Persist> map = new HashMap<Enumerate, Persist>();
        map.put(Enumerate.PROTOSS, new Persist());
        map.put(Enumerate.TERRAN, new Persist());
        map.put(Enumerate.ZERG, new Persist());
        String uuid = UUID.randomUUID().toString();
        for (int index = 0; index < size; index++) {
            final Information entity = new Information(100000, "pack", "name", map);
            accessor.createInstance(Information.class, entity);
            final Player player = new Player(index + uuid);
            accessor.createInstance(Player.class, player);
            final Item item = new Item(player.getId());
            accessor.createInstance(Item.class, item);
            if (index % 1000000 == 0) {
                logger.debug("Entity[{}],Player[{}],Item[{}]", new Object[] { entity.getId(), player.getId(), item.getId() });
            }
        }
    }

    public final long getExecuteTime(TimeUnit unit) {
        return unit.convert(total, TimeUnit.MILLISECONDS);
    }
}
