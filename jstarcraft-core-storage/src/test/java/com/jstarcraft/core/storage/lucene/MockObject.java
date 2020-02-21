package com.jstarcraft.core.storage.lucene;

import java.time.Instant;
import java.util.LinkedList;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.lucene.annotation.LuceneConfiguration;
import com.jstarcraft.core.storage.lucene.annotation.LuceneIndex;
import com.jstarcraft.core.storage.lucene.annotation.LuceneSort;
import com.jstarcraft.core.storage.lucene.annotation.LuceneStore;

@LuceneConfiguration(id = "id")
public class MockObject implements IdentityObject<Integer> {

    @LuceneIndex
    @LuceneSort
    @LuceneStore
    private Integer id;

    @LuceneIndex
    @LuceneSort
    @LuceneStore
    private String name;

    @LuceneIndex
    @LuceneSort
    @LuceneStore
    private int money;

    @LuceneIndex
    @LuceneSort
    @LuceneStore
    private Instant instant;

    @LuceneIndex
    @LuceneSort
    @LuceneStore
    private MockEnumeration race;

    @LuceneIndex
    @LuceneStore
    private LinkedList<NestObject> children;

    public MockObject() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public Instant getInstant() {
        return instant;
    }

    public MockEnumeration getRace() {
        return race;
    }

    public LinkedList<NestObject> getChildren() {
        return children;
    }

    public int[] toCurrencies() {
        return new int[] { money };
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof MockObject))
            return false;
        MockObject that = (MockObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getId(), that.getId());
        equal.append(this.getName(), that.getName());
        equal.append(this.getMoney(), that.getMoney());
        equal.append(this.getInstant(), that.getInstant());
        equal.append(this.getRace(), that.getRace());
        equal.append(this.getChildren(), that.getChildren());
        return equal.isEquals();
    }

    public static MockObject instanceOf(Integer id, String name, String childrenName, int money, Instant instant, MockEnumeration race) {
        MockObject instance = new MockObject();
        instance.id = id;
        instance.name = name;
        instance.money = money;
        instance.instant = instant;
        instance.race = race;
        instance.children = new LinkedList<>();
        for (int index = 0; index < money; index++) {
            instance.children.add(NestObject.instanceOf(index, childrenName));
        }
        return instance;
    }

}
