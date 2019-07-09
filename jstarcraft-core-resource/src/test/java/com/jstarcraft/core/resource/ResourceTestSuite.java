package com.jstarcraft.core.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.resource.json.JsonAdapterTestCase;
import com.jstarcraft.core.resource.property.PropertyAdapterTestCase;
import com.jstarcraft.core.resource.xlsx.XlsxAdapterTestCase;
import com.jstarcraft.core.resource.yaml.YamlAdapterTestCase;

@RunWith(Suite.class)
@SuiteClasses({ JsonAdapterTestCase.class, PropertyAdapterTestCase.class, XlsxAdapterTestCase.class, YamlAdapterTestCase.class })
public class ResourceTestSuite {

}
