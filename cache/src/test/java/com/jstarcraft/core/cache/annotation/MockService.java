package com.jstarcraft.core.cache.annotation;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.Assert;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jstarcraft.core.cache.CacheService;
import com.jstarcraft.core.cache.CacheState;

public class MockService implements ApplicationContextAware, InitializingBean, DisposableBean {

	enum State {
		/** 服务启动 */
		SERVICE_STARTED,

		/** 运行 */
		SERVICE_RUN,

		/** 服务停止 */
		SERVICE_STOPPED;
	}

	@Autowired
	public ApplicationContext applicationContext;

	@Autowired
	private CacheService cacheService;

	private AtomicReference<State> state = new AtomicReference<>(null);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (state.get() != null) {
			Assert.fail();
		}
	}

	@PostConstruct
	public void postConstruct() throws Exception {
		if (cacheService.getState() != null) {
			Assert.fail();
		}

		if (state.get() != null) {
			Assert.fail();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (cacheService.getState() != null) {
			Assert.fail();
		}

		if (!state.compareAndSet(null, State.SERVICE_STARTED)) {
			Assert.fail();
		}
	}

	@AfterCacheStarted
	void cacheServiceStarted() {
		if (!cacheService.getState().equals(CacheState.STARTED)) {
			Assert.fail();
		}

		if (!state.compareAndSet(State.SERVICE_STARTED, State.SERVICE_RUN)) {
			Assert.fail();
		}
	}

	@BeforeCacheStoped
	void cacheServiceStoped() throws Exception {
		if (!cacheService.getState().equals(CacheState.STARTED)) {
			Assert.fail();
		}

		if (!state.compareAndSet(State.SERVICE_RUN, State.SERVICE_STOPPED)) {
			Assert.fail();
		}
	}

	@PreDestroy
	public void preDestory() {
		if (!cacheService.getState().equals(CacheState.STOPPED)) {
			Assert.fail();
		}

		if (!state.compareAndSet(State.SERVICE_STOPPED, null)) {
			Assert.fail();
		}
	}

	@Override
	public void destroy() throws Exception {
		if (!cacheService.getState().equals(CacheState.STOPPED)) {
			Assert.fail();
		}

		if (state.get() != null) {
			Assert.fail();
		}
	}

	public State getState() {
		return state.get();
	}

}
