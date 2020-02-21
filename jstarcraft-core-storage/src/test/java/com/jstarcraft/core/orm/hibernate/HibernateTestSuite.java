package com.jstarcraft.core.orm.hibernate;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        // Hibernate访问器测试
        HibernateAccessorTestCase.class, HibernateMetadataTestCase.class })
public class HibernateTestSuite {

}
