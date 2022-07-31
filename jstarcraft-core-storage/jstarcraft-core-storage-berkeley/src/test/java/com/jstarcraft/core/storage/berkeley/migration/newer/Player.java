package com.jstarcraft.core.storage.berkeley.migration.newer;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyConfiguration;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
@BerkeleyConfiguration(store = "migration")
public class Player implements IdentityObject<Long> {

    @PrimaryKey(sequence = "Player_ID")
    private long id;

    /**
     * 旧库Player.name没有次级索引,新库Player.name有次级索引
     */
    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private String name;

    /**
     * 旧库Player.sex类型为Boolean,新库Player.sex类型为String
     */
    private String sex;

    /**
     * 旧库Player.age不存在,新库Player.age存在
     */
    private int age;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Object getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

}
