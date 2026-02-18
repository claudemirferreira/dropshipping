package com.srv.setebit.dropshipping.infrastructure.storage;

import com.srv.setebit.dropshipping.domain.product.ProductFile;
import com.srv.setebit.dropshipping.domain.product.ProductFileType;
import com.srv.setebit.dropshipping.domain.product.port.ProductFileRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductFileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "oci.object-storage.enabled", havingValue = "false", matchIfMissing = true)
public class LocalProductFileStorageService implements ProductFileStoragePort {

    private final Path uploadDir;
    private final ProductFileRepositoryPort productFileRepository;

    public LocalProductFileStorageService(@Value("${app.upload.dir:uploads}") String uploadDirPath,
                                         ProductFileRepositoryPort productFileRepository) {
        this.uploadDir = Path.of(uploadDirPath).toAbsolutePath().normalize();
        this.productFileRepository = productFileRepository;
    }

    @Override
    @Transactional
    public ProductFile upload(UUID productId, MultipartFile file, ProductFileType type, boolean isMain, Integer position, String altText) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new IllegalArgumentException("Tipo de arquivo não suportado: " + contentType);
        }

        String prefix = type == ProductFileType.IMAGE ? "imagem" : "video";
        String extension = guessExtension(contentType, file);
        String objectName = prefix + "/" + productId + "/" + UUID.randomUUID() + extension;
        Path targetFile = uploadDir.resolve(objectName);

        try {
            Files.createDirectories(targetFile.getParent());
        } catch (IOException e) {
            throw new IOException("Não foi possível criar o diretório de upload", e);
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetFile);
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
