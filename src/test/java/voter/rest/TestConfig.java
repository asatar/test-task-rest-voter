package voter.rest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.test.context.ActiveProfiles;
import voter.clock.RealTimeService;
import voter.clock.TimeService;

@SpringBootApplication
@EntityScan("voter.bean")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {"voter.manager","voter.exception","voter.rest","voter.scheduler","voter.config.web"})
@EnableJpaRepositories(basePackages = "voter.repo")
@EnableScheduling
public class TestConfig {

}
