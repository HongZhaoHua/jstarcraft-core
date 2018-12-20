package com.jstarcraft.core.distribution.lock.redis;

import java.util.Arrays;
import java.util.List;

import org.redisson.api.RScript;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;

import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.lock.DistributionDefinition;
import com.jstarcraft.core.distribution.lock.DistributionManager;
import com.jstarcraft.core.utility.SecurityUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Redis分布式管理器
 * 
 * <pre>由于Redisson已经提供许多锁方案,所以不再提供封装.</pre>
 * 
 * @author Birdy
 *
 */
public class RedisDistributionManager extends DistributionManager {

	public final static String lockScript = "local lock = redis.call('set', KEYS[1], ARGV[1], 'PX', ARGV[2], 'NX'); if (lock) then return 1; else return 0; end;";

	public final static String unlockScript = "local unlock = redis.call('get', KEYS[1]) == ARGV[1]; if (unlock) then redis.call('del', KEYS[1]); return 1; else return 0; end;";

	public final static String lockSignature = SecurityUtility.byte2Hex(SecurityUtility.signatureSha1(lockScript.getBytes(StringUtility.CHARSET)));

	public final static String unlockSignature = SecurityUtility.byte2Hex(SecurityUtility.signatureSha1(unlockScript.getBytes(StringUtility.CHARSET)));

	private RScript script;

	public RedisDistributionManager(RScript script) {
		this.script = script;
		List<Boolean> hasScripts = this.script.scriptExists(lockSignature, unlockSignature);
		if (!hasScripts.get(0)) {
			this.script.scriptLoad(lockScript);
		}
		if (!hasScripts.get(1)) {
			this.script.scriptLoad(unlockScript);
		}
	}

	@Override
	protected void lock(DistributionDefinition definition) {
		// 尝试加锁
		String key = definition.getName();
		Long value = definition.getMost().toEpochMilli();
		int expire = Long.valueOf(value - System.currentTimeMillis()).intValue();
		Object lock = script.evalSha(Mode.READ_WRITE, lockSignature, ReturnType.BOOLEAN, Arrays.asList(key), value, expire);
		if (!Boolean.class.cast(lock)) {
			throw new DistributionLockException();
		}
	}

	@Override
	protected void unlock(DistributionDefinition definition) {
		// 尝试解锁
		String key = definition.getName();
		Long value = definition.getMost().toEpochMilli();
		Object unlock = script.evalSha(Mode.READ_WRITE, unlockSignature, ReturnType.BOOLEAN, Arrays.asList(key), value);
		if (!Boolean.class.cast(unlock)) {
			throw new DistributionUnlockException();
		}
	}

}
