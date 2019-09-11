package com.jstarcraft.core.common.conversion.csv;

import java.time.Instant;
import java.util.LinkedList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.common.conversion.csv.annotation.CsvConfiguration;

@CsvConfiguration({ "id", "firstName", "lastName", "money", "instant", "list", "race" })
public class CsvObject {

    private Integer id;

    private String firstName;

    private String lastName;

    private int money;

    private Instant instant;

    private CsvEnumeration race;

    private LinkedList<MockObject> list;

    private CsvObject() {
    }

    public String[] toNames() {
        return new String[] { firstName, lastName };
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
        if (getClass() != object.getClass())
            return false;
        CsvObject that = (CsvObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.id, that.id);
        equal.append(this.firstName, that.firstName);
        equal.append(this.lastName, that.lastName);
        equal.append(this.money, that.money);
        equal.append(this.instant, that.instant);
        equal.append(this.race, that.race);
        equal.append(this.list, that.list);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(id);
        hash.append(firstName);
        hash.append(lastName);
        hash.append(money);
        hash.append(instant);
        hash.append(race);
        hash.append(list);
        return hash.toHashCode();
    }

    public static CsvObject instanceOf(Integer id, String firstName, String lastName, int money, Instant instant, CsvEnumeration race) {
        CsvObject instance = new CsvObject();
        instance.id = id;
        instance.firstName = firstName;
        instance.lastName = lastName;
        instance.money = money;
        instance.instant = instant;
        instance.race = race;
        instance.list = new LinkedList<>();
        for (int index = 0; index < money; index++) {
            instance.list.add(MockObject.instanceOf(index, lastName));
        }
        return instance;
    }

}
