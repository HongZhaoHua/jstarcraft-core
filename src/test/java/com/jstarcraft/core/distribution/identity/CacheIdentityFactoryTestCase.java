package com.jstarcraft.core.distribution.identity;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.distribution.identity.CacheIdentityFactory;
import com.jstarcraft.core.distribution.identity.IdentityFactory;
import com.jstarcraft.core.orm.OrmAccessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CacheIdentityFactoryTestCase extends IdentityFactoryTestCase {

	@Autowired
	private OrmAccessor accessor;

	@Override
	protected IdentityFactory getIdentityFactory() {
		CacheIdentityFactory identityFactory = new CacheIdentityFactory(accessor, MockObject.class, 0, 58);
		Assert.assertThat(identityFactory.getSequence(), CoreMatchers.equalTo(1L));

		long id = 10L;
		MockObject object = MockObject.instanceOf(id, "birdy", "hong", 10, 10);
		accessor.create(MockObject.class, object);
		identityFactory = new CacheIdentityFactory(accessor, MockObject.class, 0, 58);
		accessor.delete(MockObject.class, id);
		Assert.assertThat(identityFactory.getSequence(), CoreMatchers.equalTo(id + 1));
		return identityFactory;
	}

}
