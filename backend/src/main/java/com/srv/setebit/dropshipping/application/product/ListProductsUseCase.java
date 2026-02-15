package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.response.PageProductResponse;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductImageResponse;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductResponse;
import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListProductsUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort productImageRepository;

    public ListProductsUseCase(ProductRepositoryPort productRepository,
                              ProductImageRepositoryPort productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    public PageProductResponse execute(String name, String status, UUID categoryId, Pageable pageable) {
        Page<Product> products = productRepository.findAllByFilter(name, status, categoryId, pageable);
        List<ProductResponse> content = products.getContent().stream()
                .map(this::toResponse)
                .toList();
        return new PageProductResponse(
                content,
                products.getTotalElements(),
                products.getTotalPages(),
                products.getSize(),
                products.getNumber(),
                products.isFirst(),
                products.isLast()
        );
    }

    private ProductResponse toResponse(Product product) {
        String mainImageUrl = productImageRepository.findByProductIdOrderByPosition(product.getId()).stream()
                .filter(img -> img.isMain())
                .findFirst()
                .map(img -> img.getUrl())
                .orElseGet(() -> productImageRepository.findByProductIdOrderByPosition(product.getId()).stream()
                        .findFirst()
                        .map(img -> img.getUrl())
                        .orElse(null));
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getShortDescription(),
                product.getSalePrice(),
                product.getCostPrice(),
                product.getCurrency(),
                product.getStatus(),
                product.getSlug(),
                mainImageUrl,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
