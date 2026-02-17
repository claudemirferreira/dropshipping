package com.srv.setebit.dropshipping.infrastructure.storage;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.srv.setebit.dropshipping.domain.product.ProductFile;
import com.srv.setebit.dropshipping.domain.product.ProductFileType;
import com.srv.setebit.dropshipping.domain.product.port.ProductFileRepositoryPort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Service
public class ProductFileStorageService {

    private final ObjectStorage objectStorage;
    private final OciObjectStorageProperties properties;
    private final ProductFileRepositoryPort productFileRepository;

    public ProductFileStorageService(ObjectStorage objectStorage,
                                     OciObjectStorageProperties properties,
                                     ProductFileRepositoryPort productFileRepository) {
        this.objectStorage = objectStorage;
        this.properties = properties;
        this.productFileRepository = productFileRepository;
    }

    @Transactional
    public ProductFile upload(UUID productId, MultipartFile file, ProductFileType type, boolean isMain, Integer position, String altText) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new IllegalArgumentException("Tipo de arquivo não suportado: " + contentType);
        }

        String prefix = type == ProductFileType.IMAGE ? "imagem" : "video";
        String extension = guessExtension(contentType, file);
        String objectName = prefix + "/" + productId + "/" + UUID.randomUUID() + extension;

        try (InputStream in = file.getInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucketName(properties.getBucketName())
                    .namespaceName(properties.getNamespace())
                    .objectName(objectName)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .putObjectBody(in)
                    .build();

            objectStorage.putObject(request);
        }

        ProductFile productFile = new ProductFile();
        productFile.setId(UUID.randomUUID());
        productFile.setProductId(productId);
        productFile.setFileType(type);
        productFile.setObjectName(objectName);
        productFile.setOriginalName(file.getOriginalFilename());
        productFile.setPosition(position != null ? position : 0);
        productFile.setMain(isMain);
        productFile.setAltText(altText);
        productFile.setCreatedAt(Instant.now());

        return productFileRepository.save(productFile);
    }

    private String guessExtension(String contentType, MultipartFile file) {
        if (MediaType.IMAGE_JPEG_VALUE.equals(contentType)) {
            return ".jpg";
        }
        if (MediaType.IMAGE_PNG_VALUE.equals(contentType)) {
            return ".png";
        }
        if ("image/gif".equals(contentType)) {
            return ".gif";
        }
        if ("image/webp".equals(contentType)) {
            return ".webp";
        }
        if ("video/mp4".equals(contentType)) {
            return ".mp4";
        }
        if ("video/webm".equals(contentType)) {
            return ".webm";
        }
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            return original.substring(original.lastIndexOf("."));
        }
        return "";
    }
}

