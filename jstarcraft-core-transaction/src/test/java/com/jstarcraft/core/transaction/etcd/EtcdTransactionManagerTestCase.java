package com.jstarcraft.core.transaction.etcd;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;

import io.etcd.jetcd.Client;

public class EtcdTransactionManagerTestCase extends TransactionManagerTestCase {

    private static Client etcd;

    @BeforeClass
    public static void testBefore() {
        etcd = Client.builder().endpoints("http://127.0.0.1:2379").build();
    }

    @AfterClass
    public static void testAfter() {
    }

    @Override
    protected TransactionManager getDistributionManager() {
        return new EtcdTransactionManager(etcd);
    }

}
