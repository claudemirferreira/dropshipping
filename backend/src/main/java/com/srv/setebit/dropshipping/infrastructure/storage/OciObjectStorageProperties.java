package com.srv.setebit.dropshipping.infrastructure.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "oci.object-storage.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "oci.object-storage")
public class OciObjectStorageProperties {

    /**
     * Namespace do Object Storage.
     */
    private String namespace;

    /**
     * Nome do bucket onde serão gravados os arquivos de produtos.
     */
    private String bucketName;

    /**
     * Região (ex: sa-saopaulo-1).
     */
    private String region;

    /**
     * Profile do arquivo ~/.oci/config (ex: DEFAULT).
     */
    private String configProfile = "DEFAULT";

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getConfigProfile() {
        return configProfile;
    }

    public void setConfigProfile(String configProfile) {
        this.configProfile = configProfile;
    }
}

