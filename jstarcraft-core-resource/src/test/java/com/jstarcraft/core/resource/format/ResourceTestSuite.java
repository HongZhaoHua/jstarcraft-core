package com.jstarcraft.core.resource.format;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.resource.format.json.JsonFormatAdapterTestCase;
import com.jstarcraft.core.resource.format.property.PropertyFormatAdapterTestCase;
import com.jstarcraft.core.resource.format.xlsx.XlsxFormatAdapterTestCase;
import com.jstarcraft.core.resource.format.xml.XmlFormatAdapterTestCase;
import com.jstarcraft.core.resource.format.yaml.YamlFormatAdapterTestCase;

@RunWith(Suite.class)
@SuiteClasses({ JsonFormatAdapterTestCase.class, PropertyFormatAdapterTestCase.class, XlsxFormatAdapterTestCase.class, XmlFormatAdapterTestCase.class, YamlFormatAdapterTestCase.class })
public class ResourceTestSuite {

}
