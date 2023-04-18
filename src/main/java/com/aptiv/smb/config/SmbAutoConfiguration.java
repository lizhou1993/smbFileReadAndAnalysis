package com.aptiv.smb.config;

import com.aptiv.smb.util.SmbReaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(SmbAutoProperties.class)
public class SmbAutoConfiguration {
    @Bean
    SmbReaderUtil SmbReaderUtil(SmbAutoProperties smbAutoProperties) {
        return new SmbReaderUtil(smbAutoProperties);
    }
}
