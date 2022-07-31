package com.jstarcraft.core.storage.berkeley.persistent;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class Item {

    /** 物品 */
    private int articleId;

    /** 数量 */
    private AtomicInteger count;

    private Item() {
    }

    public Item(int articleId, int count) {
        this.articleId = articleId;
        this.count = new AtomicInteger(count);
    }

    public int getArticleId() {
        return articleId;
    }

    public int getCount() {
        return count.get();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof Item))
            return false;
        Item that = (Item) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.articleId, that.articleId);
        equal.append(this.count, that.count);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(articleId);
        hash.append(count);
        return hash.toHashCode();
    }

}
