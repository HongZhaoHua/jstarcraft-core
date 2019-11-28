package com.jstarcraft.core.orm.neo4j;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import com.jstarcraft.core.orm.hibernate.MockObject;
import com.jstarcraft.core.utility.StringUtility;

@RelationshipEntity
public class MockRelation {

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
