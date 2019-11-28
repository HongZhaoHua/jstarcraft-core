package com.jstarcraft.core.orm.neo4j;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import com.jstarcraft.core.orm.hibernate.MockObject;
import com.jstarcraft.core.utility.StringUtility;

@NodeEntity
public class MockNode {

    @Id
    private long id;

    @Property
    @Index
    private String name;

    MockNode() {
    }

    public MockNode(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof MockObject))
            return false;
        MockNode that = (MockNode) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getId(), that.getId());
        equal.append(this.getName(), that.getName());
        return equal.isEquals();
    }

    @Override
    public String toString() {
        return StringUtility.reflect(this);
    }

}
