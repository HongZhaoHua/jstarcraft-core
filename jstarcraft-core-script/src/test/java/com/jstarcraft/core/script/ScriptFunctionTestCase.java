package com.jstarcraft.core.script;

import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.reflection.ReflectionUtility;

public abstract class ScriptFunctionTestCase {

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public static double fibonacci(int number) {
        if (number == 0) {
            return 0;
        } else if (number == 1 || number == 2) {
            return 1;
        } else {
            double index = 3;
            double left = 1;
            double right = 1;
            while (index++ <= number) {
                right = right + left;
                left = right - left;
            }
            return right;
        }
    }

    protected abstract ScriptFunction getMethodFunction(ScriptContext context);

    protected abstract ScriptFunction getObjectFunction(ScriptContext context);

    protected abstract ScriptFunction getFibonacciFunction(ScriptContext context);

    @Test
    public void testMethod() throws Exception {
        ScriptContext context = new ScriptContext();
        // 此处故意使用ScriptExpressionTestCase.class,测试是否冲突.
        context.useClasses(ScriptFunctionTestCase.class);
        ReflectionUtility.doWithLocalMethods(ScriptFunctionTestCase.class, (method) -> {
            if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                context.useMethod(method.getName() + "Method", method);
            }
        });
        ScriptFunction function = getMethodFunction(context);
        int number = 10;
        Number fibonacci = function.doWith(Number.class, number);
        Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
    }

    @Test
    public void testObject() {
        ScriptContext context = new ScriptContext();
        context.useClass("Mock", MockObject.class);
        context.useClasses(Instant.class, MockEnumeration.class);
        ScriptFunction function = getObjectFunction(context);
        MockObject object = function.doWith(MockObject.class, 0, 10);
        Assert.assertThat(object.getId(), CoreMatchers.equalTo(0));
        Assert.assertThat(object.getName(), CoreMatchers.equalTo("birdy"));
        Assert.assertThat(object.getChildren().size(), CoreMatchers.equalTo(10));
    }

    @Test
    public void testSerial() throws Exception {
        int size = 50;
        ScriptContext context = new ScriptContext();
        ScriptFunction function = getFibonacciFunction(context);
        for (int index = 0; index < size; index++) {
            int number = index + 2;
            Number fibonacci = function.doWith(Number.class, number);
            Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
        }
    }

    @Test
    public void testParallel() throws Exception {
        int size = 50;
        CountDownLatch latch = new CountDownLatch(size);
        ScriptContext context = new ScriptContext();
        ScriptFunction function = getFibonacciFunction(context);
        for (int index = 0; index < size; index++) {
            int number = index + 2;
            executor.execute(() -> {
                Number fibonacci = function.doWith(Number.class, number);
                Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

}
