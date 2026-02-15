package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort productImageRepository;

    public DeleteProductUseCase(ProductRepositoryPort productRepository,
                               ProductImageRepositoryPort productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    public void execute(UUID id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new ProductNotFoundException(id);
        }
        productImageRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
}
