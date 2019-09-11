package com.jstarcraft.core.script;

import java.time.Instant;
import java.util.LinkedList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MockObject {

    private Integer id;

    private String name;

    private int money;

    private Instant instant;

    private MockEnumeration race;

    private LinkedList<NestObject> children;

    public MockObject(Integer id, String name, String childrenName, int money, Instant instant, MockEnumeration race) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.instant = instant;
        this.race = race;
        this.children = new LinkedList<>();
        for (int index = 0; index < money; index++) {
            this.children.add(NestObject.instanceOf(index, childrenName));
        }
    }

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
        equal.append(this.id, that.id);
        equal.append(this.name, that.name);
        equal.append(this.money, that.money);
        equal.append(this.instant, that.instant);
        equal.append(this.race, that.race);
        equal.append(this.children, that.children);
        return equal.isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder string = new ToStringBuilder(this);
        string.append(id);
        string.append(name);
        string.append(money);
        string.append(instant);
        string.append(race);
        string.append(children);
        return string.toString();
    }

}
