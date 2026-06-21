package com.keepguard.ms_user;

import com.keepguard.lib_common.config.MetricsConfig;
import com.keepguard.lib_security.annotation.EnableJwtSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.keepguard.ms_user", "com.keepguard.lib_common", "com.keepguard.lib_security"})
@EnableJpaRepositories(basePackages = "com.keepguard.ms_user.infrastructure.persistence.spring")
@EnableJpaAuditing
@EnableJwtSecurity
@Import({
    MetricsConfig.class
})
public class MsUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsUserApplication.class, args);
    }
}
