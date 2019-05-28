package com.jstarcraft.core.orm.mybatis;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.jstarcraft.core.common.identification.IdentityObject;

@Entity
@TableName("MockObject")
public class MockObject implements IdentityObject<Integer> {

	@Id
	@TableId
	private Integer id;

	private String name;

	private int money;

	private Instant instant;

	@Enumerated(EnumType.STRING)
	private MockEnumeration race;

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
		return equal.isEquals();
	}

	public static MockObject instanceOf(Integer id, String name, String childrenName, int money, Instant instant, MockEnumeration race) {
		MockObject instance = new MockObject();
		instance.id = id;
		instance.name = name;
		instance.money = money;
		instance.instant = instant;
		instance.race = race;
		return instance;
	}

}
