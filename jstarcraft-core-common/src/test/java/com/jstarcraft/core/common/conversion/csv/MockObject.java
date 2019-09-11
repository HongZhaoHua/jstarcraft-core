package com.jstarcraft.core.common.conversion.csv;

import java.util.HashMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.common.conversion.csv.annotation.CsvConfiguration;

@CsvConfiguration({ "id", "name", "map" })
public class MockObject {

    private long id;

    private String name;

    private HashMap<String, Long> map;

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MockObject that = (MockObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.id, that.id);
        equal.append(this.name, that.name);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(id);
        hash.append(name);
        return hash.toHashCode();
    }

    public static MockObject instanceOf(long id, String name) {
        MockObject object = new MockObject();
        object.id = id;
        object.name = name;
        object.map = new HashMap<>(1);
        object.map.put(name, id);
        return object;
    }

}
