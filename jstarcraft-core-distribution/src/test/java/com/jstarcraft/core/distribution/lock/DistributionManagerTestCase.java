package com.jstarcraft.core.distribution.lock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.utility.StringUtility;

public abstract class DistributionManagerTestCase {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected final String name = "jstarcraft";

	protected abstract DistributionManager getDistributionManager();

	@Test
	public void test() {
		try {
			DistributionManager manager = getDistributionManager();

			{
				Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
				DistributionDefinition definition = new DistributionDefinition(name, most);
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
				DistributionDefinition definition = new DistributionDefinition(name, most);
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
