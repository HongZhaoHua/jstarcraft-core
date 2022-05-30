package com.jstarcraft.core.storage.neo4j;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.utility.StringUtility;

@NodeEntity
public class MockNode implements IdentityObject<Integer> {

    // TODO 注意:Neo4j-OGM对原始类型支持似乎有问题,必须使用包装类型,否则Session.load无法装载到指定对象.
    @Id
    private Integer id;
//    private int id;

    @Property
    @Index
    private String name;

    private int money;

    private MockEnumeration race;

    MockNode() {
    }

    public MockNode(int id, String name, int money, MockEnumeration race) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.race = race;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public MockEnumeration getRace() {
        return race;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof MockNode))
            return false;
        MockNode that = (MockNode) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getId(), that.getId());
        equal.append(this.getName(), that.getName());
        equal.append(this.getMoney(), that.getMoney());
        equal.append(this.getRace(), that.getRace());
        return equal.isEquals();
    }

    @Override
    public String toString() {
        return StringUtility.reflect(this);
    }

}
