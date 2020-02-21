package com.jstarcraft.core.storage.identification;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.jstarcraft.core.common.identification.IdentityObject;

@Entity
public class MockObject implements IdentityObject<Long> {

    @Id
    private Long id;

    private String firstName;

    private String lastName;

    private int money;

    private int token;

    public MockObject() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMoney() {
        return money;
    }

    public int getToken() {
        return token;
    }

    public String[] toNames() {
        return new String[] { firstName, lastName };
    }

    public int[] toCurrencies() {
        return new int[] { money, token };
    }

    public static MockObject instanceOf(Long id, String firstName, String lastName, int money, int token) {
        MockObject instance = new MockObject();
        instance.id = id;
        instance.firstName = firstName;
        instance.lastName = lastName;
        instance.money = money;
        instance.token = token;
        return instance;
    }

}
