package com.jstarcraft.core.monitor.trace;

import org.junit.Test;

public class TracerTestCase {

    @Test
    public void test() {
        outer();
    }

    public void outer() {
        inner();
    }

    public void inner() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            System.out.println(element);
        }
    }

    @Test
    public void testGetClassName() {

        // 方法1：通过SecurityManager的保护方法getClassContext()

        String clazzName1 = new SecurityManager() {

            public String getClassName() {

                return getClassContext()[1].getName();

            }

        }.getClassName();

        System.out.println("clazzName1 is " + clazzName1);

        // 方法2：通过Throwable的方法getStackTrace()

        String clazzName2 = new Throwable().getStackTrace()[0].getClassName();

        System.out.println("clazzName2 is " + clazzName2);

        // 方法3：通过分析匿名类名称()

        String clazzName3 = new Object() {

            public String getClassName() {

                String clazzName = this.getClass().getName();

                return clazzName.substring(0, clazzName.lastIndexOf('$'));

            }

        }.getClassName();

        System.out.println("clazzName3 is " + clazzName3);

        // 方法4：通过Thread的方法getStackTrace()

        String clazzName4 = Thread.currentThread().getStackTrace()[1].getClassName();

        System.out.println("clazzName4 is " + clazzName4);

    }

    @Test
    public void testGetFunctionName() {

        // 方法1：通过Throwable的方法getStackTrace()

        String funcName1 = new Throwable().getStackTrace()[0].getMethodName();

        System.out.println("funcName1 is " + funcName1);

        // 方法2：通过Thread的方法getStackTrace()

        String funcName2 = Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println("funcName2 is " + funcName2);

    }

}
