package com.jstarcraft.core.storage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.storage.json.JsonAdapterTestCase;
import com.jstarcraft.core.storage.property.PropertyAdapterTestCase;
import com.jstarcraft.core.storage.xlsx.XlsxAdapterTestCase;
import com.jstarcraft.core.storage.yaml.YamlAdapterTestCase;

@RunWith(Suite.class)
@SuiteClasses({ JsonAdapterTestCase.class, PropertyAdapterTestCase.class, XlsxAdapterTestCase.class, YamlAdapterTestCase.class })
public class StroageTestSuite {

}
