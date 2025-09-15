package studio.lab9.socks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

@Configuration
public class TestConfig {
	public static class DotenvTestExecutionListener extends AbstractTestExecutionListener {
		@Override
		public void beforeTestClass(TestContext testContext) {

		}
	}
}