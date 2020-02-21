package com.jstarcraft.core.storage.neo4j;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.hibernate.MockObject;
import com.jstarcraft.core.utility.StringUtility;

@RelationshipEntity
public class MockRelation implements IdentityObject<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;

    @StartNode
    private MockNode from;

    @EndNode
    private MockNode to;

    MockRelation() {
    }

    public MockRelation(String name, MockNode from, MockNode to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public MockNode getFrom() {
        return from;
    }

    public MockNode getTo() {
        return to;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof MockObject))
            return false;
        MockRelation that = (MockRelation) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.getId(), that.getId());
        equal.append(this.getName(), that.getName());
        equal.append(this.getFrom(), that.getFrom());
        equal.append(this.getTo(), that.getTo());
        return equal.isEquals();
    }

    @Override
    public String toString() {
        return StringUtility.reflect(this);
    }

}
