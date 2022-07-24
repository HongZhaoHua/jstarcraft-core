package com.jstarcraft.core.resource.format;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.jstarcraft.core.common.conversion.xml.XmlUtility.Wrapper;
import com.jstarcraft.core.resource.exception.StorageException;

/**
 * XML适配器
 * 
 * @author Birdy
 *
 */
public class XmlFormatAdapter implements FormatAdapter {

    @Override
    public <E> Iterator<E> iterator(Class<E> clazz, InputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(Wrapper.class, clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Wrapper<E> wrapper = (Wrapper<E>) unmarshaller.unmarshal(new StreamSource(stream), Wrapper.class).getValue();
            List<E> instances = wrapper.getInstances();
            return instances.iterator();
        } catch (Exception exception) {
            throw new StorageException("遍历XML异常", exception);
        }
    }

}
