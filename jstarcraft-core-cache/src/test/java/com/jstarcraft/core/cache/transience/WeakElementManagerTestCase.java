package com.jstarcraft.core.cache.transience;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.cache.MockEntityObject;
import com.jstarcraft.core.cache.transience.TransienceElement;
import com.jstarcraft.core.cache.transience.WeakElementManager;

public class WeakElementManagerTestCase {

    @Test
    public void test() throws Exception {
        int size = 1000;
        // 元素持有者
        Map<Integer, TransienceElement> elementHolder = new HashMap<>();
        // 元素管理者
        WeakElementManager<Integer, MockEntityObject> elementManager = new WeakElementManager<>(null);

        for (int index = 0; index < size; index++) {
            MockEntityObject object = MockEntityObject.instanceOf(index, "birdy" + index, "hong", index, index);
            MockEntityObject other = MockEntityObject.instanceOf(index, "wolfy" + index, "xiao", index, index);
            TransienceElement element = elementManager.putElement(object);
            elementHolder.put(index, element);

            /** 如果{@link CacheObject#getId}相同,则认为是相等. */
            element = elementManager.putElement(other);
            if (element.getCacheObject() != object) {
                Assert.fail();
            }
            element = elementManager.getElement(object);
            if (element.getCacheObject() != object) {
                Assert.fail();
            }
            element = elementManager.getElement(other);
            if (element.getCacheObject() != object) {
                Assert.fail();
            }
        }

        // 测试垃圾回收
        System.gc();
        Thread.sleep(1000);
        Assert.assertThat(elementManager.getCount(), CoreMatchers.equalTo(size));
        elementHolder.clear();
        System.gc();
        Thread.sleep(1000);
        Assert.assertThat(elementManager.getCount(), CoreMatchers.equalTo(0));
    }

}
