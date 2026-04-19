package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.entity.ProductEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable.CommercialEmbeddable;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable.LogisticsEmbeddable;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable.StockEmbeddable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;

    public ProductRepositoryAdapter(@Lazy ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku).map(this::toDomain);
    }

    @Override
    public Optional<Product> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public Optional<Product> findByEan(String ean) {
        return jpaRepository.findByEan(ean).map(this::toDomain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuAndIdNot(String sku, UUID id) {
        return jpaRepository.existsBySkuAndIdNot(sku, id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public boolean existsBySlugAndIdNot(String slug, UUID id) {
        return jpaRepository.existsBySlugAndIdNot(slug, id);
    }

    @Override
    public boolean existsByEan(String ean) {
        return jpaRepository.existsByEan(ean);
    }

    @Override
    public Page<Product> findAllByFilter(String name, String status, UUID categoryId, Pageable pageable) {
        ProductStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = ProductStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return jpaRepository.findAllByFilter(name, statusEnum, categoryId, pageable).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setSku(product.getSku());
        entity.setName(product.getName());
        entity.setShortDescription(product.getShortDescription());
        entity.setFullDescription(product.getFullDescription());
        entity.setSalePrice(product.getSalePrice());
        entity.setCurrency(product.getCurrency());
        entity.setStatus(product.getStatus());
        entity.setSupplierSku(product.getSupplierSku());
        entity.setSupplierName(product.getSupplierName());
        entity.setSupplierProductUrl(product.getSupplierProductUrl());
        LogisticsEmbeddable logistics = new LogisticsEmbeddable();
        logistics.setLeadTimeEnvioDias(product.getLeadTimeDays());
        logistics.setPesoKg(product.getWeight());
        logistics.setComprimentoCm(product.getLength());
        logistics.setLarguraCm(product.getWidth());
        logistics.setAlturaCm(product.getHeight());
        entity.setLogistica(logistics);
        entity.setDropship(product.isDropship());
        entity.setSlug(product.getSlug());
        entity.setCategoryId(product.getCategoryId());
        entity.setBrand(product.getBrand());
        entity.setMetaTitle(product.getMetaTitle());
        entity.setMetaDescription(product.getMetaDescription());
        entity.setCompareAtPrice(product.getCompareAtPrice());
        StockEmbeddable estoque = new StockEmbeddable();
        estoque.setAtual(product.getStockQuantity());
        estoque.setMinimo(product.getStockMinimum());
        entity.setEstoque(estoque);
        CommercialEmbeddable comercial = new CommercialEmbeddable();
        comercial.setValorCusto(product.getCostPrice());
        comercial.setTaxaSellerPercent(product.getSellerFeePercent());
        comercial.setGarantia(product.getWarranty());
        entity.setComercial(comercial);
        entity.setTags(product.getTags());
        entity.setAttributes(product.getAttributes());
        entity.setEan(product.getEan());
        entity.setEanInterno(product.isEanInterno());
        entity.setCreatedBy(product.getCreatedBy());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());
        return entity;
    }

    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getShortDescription(),
                entity.getFullDescription(),
                entity.getSalePrice(),
                entity.getComercial() != null ? entity.getComercial().getValorCusto() : null,
                entity.getCurrency(),
                entity.getStatus(),
                entity.getSupplierSku(),
                entity.getSupplierName(),
                entity.getSupplierProductUrl(),
                entity.getLogistica() != null ? entity.getLogistica().getLeadTimeEnvioDias() : null,
                entity.isDropship(),
                entity.getLogistica() != null ? entity.getLogistica().getPesoKg() : null,
                entity.getLogistica() != null ? entity.getLogistica().getComprimentoCm() : null,
                entity.getLogistica() != null ? entity.getLogistica().getLarguraCm() : null,
                entity.getLogistica() != null ? entity.getLogistica().getAlturaCm() : null,
                entity.getSlug(),
                entity.getCategoryId(),
                entity.getBrand(),
                entity.getMetaTitle(),
                entity.getMetaDescription(),
                entity.getCompareAtPrice(),
                entity.getEstoque() != null ? entity.getEstoque().getAtual() : null,
                entity.getTags(),
                entity.getAttributes(),
                entity.getEan(),
                entity.isEanInterno(),
                entity.getEstoque() != null ? entity.getEstoque().getMinimo() : null,
                entity.getComercial() != null ? entity.getComercial().getTaxaSellerPercent() : null,
                entity.getComercial() != null ? entity.getComercial().getGarantia() : null,
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
