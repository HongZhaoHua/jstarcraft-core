package com.jstarcraft.core.storage.berkeley.migration.newer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyConfiguration;
import com.jstarcraft.core.storage.berkeley.migration.persistent.Enumerate;
import com.jstarcraft.core.storage.berkeley.migration.persistent.Persist;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
@BerkeleyConfiguration(store = "migration")
public class Information implements IdentityObject<Long> {

    @PrimaryKey(sequence = "Entity_ID")
    private long id;

    private int owner;

    private String pack;

    private String name;

    private Persist persist;

    private Map<Enumerate, Persist> map;

    private List<Persist> list;

    private AtomicInteger integer;

    Information() {
    }

    public Information(int owner, String pack, String name, Map<Enumerate, Persist> map) {
        this.owner = owner;
        this.pack = pack;
        this.name = name;
        this.map = map;
        this.persist = new Persist();
        this.list = new ArrayList<Persist>();
        for (int index = 0; index < 5; index++) {
            this.list.add(this.persist);
        }
        integer = new AtomicInteger();
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    public String getPack() {
        return pack;
    }

    public String getName() {
        return name;
    }

    public Map<Enumerate, Persist> getMap() {
        return map;
    }

    public AtomicInteger getInteger() {
        return integer;
    }

}
