package com.jstarcraft.core.cache.crud;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.cache.crud.berkeley.BerkeleyCrudTestCase;
import com.jstarcraft.core.cache.crud.hibernate.HibernateCrudTestCase;
import com.jstarcraft.core.cache.crud.mongo.MongoCrudTestCase;
import com.jstarcraft.core.cache.crud.mybatis.MyBatisCrudTestCase;

@RunWith(Suite.class)
@SuiteClasses({ BerkeleyCrudTestCase.class, HibernateCrudTestCase.class, MongoCrudTestCase.class, MyBatisCrudTestCase.class })
public class CrudTestSuite {

}
