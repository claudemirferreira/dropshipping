package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteProductUseCase {

    private final ProductRepositoryPort productRepository;

    public DeleteProductUseCase(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void execute(UUID id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
