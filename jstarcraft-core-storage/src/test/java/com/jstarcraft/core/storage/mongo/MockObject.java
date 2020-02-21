package com.jstarcraft.core.storage.mongo;

import java.time.Instant;
import java.util.LinkedList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jstarcraft.core.common.identification.IdentityObject;

@Document
public class MockObject implements IdentityObject<Integer> {

    @Id
    private Integer id;

    @Indexed
    private String name;

    private int money;

    private Instant instant;

    private MockEnumeration race;

    private LinkedList<NestObject> children;

    @Version
    private long version;

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
