package com.jstarcraft.core.transaction.resource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;
import com.jstarcraft.core.transaction.resource.ResourceDefinition;
import com.jstarcraft.core.transaction.resource.ResourceManager;
import com.jstarcraft.core.utility.StringUtility;

public abstract class ResourceManagerTestCase {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected final String name = "jstarcraft";

	protected abstract ResourceManager getDistributionManager();

	@Test
	public void test() {
		try {
			ResourceManager manager = getDistributionManager();

			{
				Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
				ResourceDefinition definition = new ResourceDefinition(name, most);
				manager.lock(definition);
				try {
					manager.lock(definition);
					Assert.fail();
				} catch (TransactionLockException exception) {
				}
				manager.unlock(definition);
			}

			{
				Instant most = Instant.now().plus(1, ChronoUnit.SECONDS);
				ResourceDefinition definition = new ResourceDefinition(name, most);
				manager.lock(definition);
				Thread.sleep(1500);
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
