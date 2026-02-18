package com.srv.setebit.dropshipping.domain.product.port;

import com.srv.setebit.dropshipping.domain.product.ProductFile;
import com.srv.setebit.dropshipping.domain.product.ProductFileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ProductFileStoragePort {

    ProductFile upload(UUID productId, MultipartFile file, ProductFileType type, boolean isMain, Integer position, String altText) throws IOException;
}
