package com.jstarcraft.core.script;

import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.reflection.ReflectionUtility;

public abstract class ScriptExpressionTestCase {

    protected static final ClassLoader loader = ScriptExpressionTestCase.class.getClassLoader();

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

    protected abstract ScriptExpression getMethodExpression(ScriptContext context);

    protected abstract ScriptExpression getObjectExpression(ScriptContext context);

    protected abstract ScriptExpression getFibonacciExpression(ScriptContext context);

    protected abstract ScriptExpression getLoadExpression(ScriptContext context);

    @Test
    public void testMethod() throws Exception {
        ScriptContext context = new ScriptContext();
        // 此处故意使用ScriptExpressionTestCase.class,测试是否冲突.
        context.useClasses(ScriptExpressionTestCase.class);
        ReflectionUtility.doWithLocalMethods(ScriptExpressionTestCase.class, (method) -> {
            if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                context.useMethod(method.getName() + "Method", method);
            }
        });
        ScriptExpression expression = getMethodExpression(context);
        Map<String, Object> scope = new HashMap<>();
        int number = 10;
        scope.put("number", number);
        Number fibonacci = expression.doWith(Number.class, scope);
        Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
    }

    @Test
    public void testObject() {
        ScriptContext context = new ScriptContext();
        context.useClass("Mock", MockObject.class);
        context.useClasses(Instant.class, MockEnumeration.class);
        ScriptExpression expression = getObjectExpression(context);
        Map<String, Object> scope = new HashMap<>();
        scope.put("index", 0);
        scope.put("size", 10);
        MockObject object = expression.doWith(MockObject.class, scope);
        Assert.assertThat(object.getId(), CoreMatchers.equalTo(0));
        Assert.assertThat(object.getName(), CoreMatchers.equalTo("birdy"));
        Assert.assertThat(object.getChildren().size(), CoreMatchers.equalTo(10));
    }

    @Test
    public void testSerial() throws Exception {
        int size = 50;
        ScriptContext context = new ScriptContext();
        ScriptExpression expression = getFibonacciExpression(context);
        for (int index = 0; index < size; index++) {
            int number = index + 2;
            Map<String, Object> scope = new HashMap<>();
            scope.put("size", number);
            Number fibonacci = expression.doWith(Number.class, scope);
            Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
        }
    }

    @Test
    public void testParallel() throws Exception {
        int size = 50;
        ExecutorService executor = Executors.newFixedThreadPool(size);
        CyclicBarrier barrier = new CyclicBarrier(size);
        CountDownLatch latch = new CountDownLatch(size);
        ScriptContext context = new ScriptContext();
        ScriptExpression left = getFibonacciExpression(context);
        ScriptExpression right = getFibonacciExpression(context);
        for (int index = 0; index < size; index++) {
            int number = index + 2;
            ScriptExpression expression = index % 2 == 0 ? left : right;
            executor.execute(() -> {
                try {
                    barrier.await();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("size", number);
                    Number fibonacci = expression.doWith(Number.class, scope);
                    Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
                    latch.countDown();
                } catch (Exception exception) {
                    Assert.fail();
                }
            });
        }
        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void testLoad() {
        ScriptContext context = new ScriptContext();
        ScriptExpression expression = getLoadExpression(context);
        Map<String, Object> scope = new HashMap<>();
        scope.put("loader", loader);
        Assert.assertEquals(MockObject.class, expression.doWith(Class.class, scope));
    }

}
