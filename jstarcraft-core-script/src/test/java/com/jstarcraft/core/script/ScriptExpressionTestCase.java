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

public abstract class ScriptExpressionTestCase {

    private ScriptScope scope = new ScriptScope();

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

    protected abstract ScriptExpression getMethodExpression(ScriptContext context, ScriptScope scope);

    protected abstract ScriptExpression getObjectExpression(ScriptContext context, ScriptScope scope);

    protected abstract ScriptExpression getFibonacciExpression(ScriptContext context, ScriptScope scope);

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
        ScriptExpression expression = getMethodExpression(context, scope);
        ScriptScope scope = expression.getScope();
        int number = 10;
        scope.createAttribute("number", number);
        Number fibonacci = expression.doWith(Number.class);
        Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
    }

    @Test
    public void testObject() {
        ScriptContext context = new ScriptContext();
        context.useClass("Mock", MockObject.class);
        context.useClasses(Instant.class, MockEnumeration.class);
        ScriptExpression expression = getObjectExpression(context, scope);
        ScriptScope scope = expression.getScope();
        scope.createAttribute("index", 0);
        scope.createAttribute("size", 10);
        MockObject object = expression.doWith(MockObject.class);
        Assert.assertThat(object.getId(), CoreMatchers.equalTo(0));
        Assert.assertThat(object.getName(), CoreMatchers.equalTo("birdy"));
        Assert.assertThat(object.getChildren().size(), CoreMatchers.equalTo(10));
    }

    @Test
    public void testSerial() throws Exception {
        int size = 50;
        ScriptContext context = new ScriptContext();
        ScriptExpression expression = getFibonacciExpression(context, scope);
        for (int index = 0; index < size; index++) {
            int number = index + 2;
            ScriptScope scope = expression.getScope();
            scope.createAttribute("size", number);
            Number fibonacci = expression.doWith(Number.class);
            Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
            scope.deleteAttributes();
        }
    }

    @Test
    public void testParallel() throws Exception {
        int size = 10;
        CountDownLatch latch = new CountDownLatch(size);
        ScriptContext context = new ScriptContext();
        ScriptExpression expression = getFibonacciExpression(context, scope);
        for (int index = 0; index < size; index++) {
            int number = index + 2;
            executor.execute(() -> {
                ScriptScope scope = expression.getScope();
                scope.createAttribute("size", number);
                Number fibonacci = expression.doWith(Number.class);
                Assert.assertThat(fibonacci.doubleValue(), CoreMatchers.equalTo(fibonacci(number)));
                scope.deleteAttributes();
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

}
