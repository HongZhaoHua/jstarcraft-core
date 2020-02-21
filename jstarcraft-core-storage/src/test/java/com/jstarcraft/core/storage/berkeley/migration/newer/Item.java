package com.jstarcraft.core.storage.berkeley.migration.newer;

import java.util.Random;
import java.util.UUID;

import javax.persistence.Version;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyConfiguration;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
@BerkeleyConfiguration(store = "migration")
public class Item implements IdentityObject<Long> {

    private static final Random random = new Random();

    @PrimaryKey(sequence = "Item_ID")
    private long id;

    /**
     * 旧库Item.playerId没有依赖Player,新库Item.playerId依赖Player
     */
    @SecondaryKey(relate = Relationship.ONE_TO_ONE, relatedEntity = Player.class)
    private long playerId;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private int articleId;

    @Version
    protected int version;

    private int amount;

    private String description;

    @SuppressWarnings("unused")
    private Item() {
    }

    public Item(long playerId) {
        this.playerId = playerId;
        this.articleId = random.nextInt(1000);
        this.amount = random.nextInt(10);
        this.description = UUID.randomUUID().toString();
    }

    public String toString() {
        return "[" + this.playerId + "," + this.articleId + "]";
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getPlayerId() {
        return playerId;
    }

    public int getArticleId() {
        return articleId;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

}
