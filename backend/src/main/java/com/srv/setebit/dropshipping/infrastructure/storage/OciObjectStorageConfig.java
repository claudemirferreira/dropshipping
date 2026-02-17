package com.srv.setebit.dropshipping.infrastructure.storage;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class OciObjectStorageConfig {

    @Bean
    public ObjectStorageClient objectStorageClient(OciObjectStorageProperties properties) throws IOException {
        ConfigFileAuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider("~/.oci/config", properties.getConfigProfile());

        ObjectStorageClient client = ObjectStorageClient.builder()
                .region(Region.fromRegionId(properties.getRegion()))
                .build(provider);

        return client;
    }
}

