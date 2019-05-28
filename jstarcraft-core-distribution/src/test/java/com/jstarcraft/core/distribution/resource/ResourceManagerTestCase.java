package com.jstarcraft.core.distribution.resource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.resource.ResourceDefinition;
import com.jstarcraft.core.distribution.resource.ResourceManager;
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
				} catch (DistributionLockException exception) {
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
				} catch (DistributionUnlockException exception) {
				}
			}
		} catch (Exception exception) {
			logger.error(StringUtility.EMPTY, exception);
			Assert.fail();
		}
	}

}
