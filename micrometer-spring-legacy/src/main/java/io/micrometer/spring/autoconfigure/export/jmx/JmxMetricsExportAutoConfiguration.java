/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring.autoconfigure.export.jmx;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import io.micrometer.spring.autoconfigure.CompositeMeterRegistryAutoConfiguration;
import io.micrometer.spring.autoconfigure.MetricsAutoConfiguration;
import io.micrometer.spring.autoconfigure.export.StringToDurationConverter;
import io.micrometer.spring.autoconfigure.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for exporting metrics to JMX.
 *
 * @author Jon Schneider
 */
@Configuration
@AutoConfigureBefore({CompositeMeterRegistryAutoConfiguration.class,
        SimpleMetricsExportAutoConfiguration.class})
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnBean(Clock.class)
@ConditionalOnClass(JmxMeterRegistry.class)
@ConditionalOnProperty(prefix = "management.metrics.export.jmx", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(JmxProperties.class)
@Import(StringToDurationConverter.class)
public class JmxMetricsExportAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JmxConfig jmxConfig(JmxProperties props) {
        return new JmxPropertiesConfigAdapter(props);
    }

    @Bean
    @ConditionalOnMissingBean
    public JmxMeterRegistry jmxMeterRegistry(JmxConfig config, HierarchicalNameMapper nameMapper, Clock clock) {
        return new JmxMeterRegistry(config, clock, nameMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public HierarchicalNameMapper hierarchicalNameMapper() {
        return HierarchicalNameMapper.DEFAULT;
    }

}
