package com.aptiv.smb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("aptiv.smb")
public class SmbAutoProperties {
    private String hostname;
    private String username;
    private String password;
    private String rootFolder;
}
