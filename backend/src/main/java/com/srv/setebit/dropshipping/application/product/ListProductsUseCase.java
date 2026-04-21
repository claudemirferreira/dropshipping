package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.response.PageProductResponse;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductResponse;
import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ListProductsUseCase {

    private final ProductRepositoryPort productRepository;

    public ListProductsUseCase(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    public PageProductResponse execute(String name, String status, UUID categoryId, Pageable pageable) {
        Page<Product> page = productRepository.findAllByFilter(name, status, categoryId, pageable);
        return new PageProductResponse(
                page.getContent().stream()
                        .map(this::toResponse)
                        .toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast()
        );
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getShortDescription(),
                p.getSalePrice(),
                p.getCostPrice(),
                p.getCurrency(),
                p.getStatus(),
                p.getSlug(),
                null,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}

