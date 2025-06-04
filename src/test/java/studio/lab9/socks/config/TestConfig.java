package studio.lab9.socks.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

@Configuration
public class TestConfig {
    static {
        Dotenv.configure().directory("./").ignoreIfMissing().load();
    }

    public static class DotenvTestExecutionListener extends AbstractTestExecutionListener {
        @Override
        public void beforeTestClass(TestContext testContext) {
            Dotenv.configure().directory("./").ignoreIfMissing().load();
        }
    }
}