package com.jstarcraft.core.transaction.consul;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;
import com.jstarcraft.core.utility.StringUtility;

public class ConsulTransactionManagerTestCase extends TransactionManagerTestCase {

    private static ConsulClient consul;

    @BeforeClass
    public static void testBefore() {
        ConsulRawClient client = new ConsulRawClient("localhost", 8500);
        consul = new ConsulClient(client);
    }

    @AfterClass
    public static void testAfter() {
    }

    @Override
    protected TransactionManager getDistributionManager() {
        return new ConsulTransactionManager(consul);
    }

    @Test
    public void test() {
        try {
            TransactionManager manager = getDistributionManager();

            {
                Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
                TransactionDefinition definition = new TransactionDefinition(name, most);
                manager.lock(definition);
                try {
                    manager.lock(definition);
                    Assert.fail();
                } catch (TransactionLockException exception) {
                }
                manager.unlock(definition);
            }

            {
                Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
                TransactionDefinition definition = new TransactionDefinition(name, most);
                manager.lock(definition);
                Thread.sleep(20000);
                try {
                    manager.unlock(definition);
                    Assert.fail();
                } catch (TransactionUnlockException exception) {
                }
            }
        } catch (Exception exception) {
            logger.error(StringUtility.EMPTY, exception);
            Assert.fail();
        }
    }

}
