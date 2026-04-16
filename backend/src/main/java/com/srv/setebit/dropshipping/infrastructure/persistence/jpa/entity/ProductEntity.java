package com.srv.setebit.dropshipping.infrastructure.persistence.jpa.entity;

import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable.CommercialEmbeddable;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable.LogisticsEmbeddable;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable.StockEmbeddable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "short_description", nullable = false, length = 500)
    private String shortDescription;

    @Column(name = "full_description", columnDefinition = "TEXT")
    private String fullDescription;

    @Column(name = "sale_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal salePrice;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "BRL";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ProductStatus status;

    @Column(name = "supplier_sku", length = 100)
    private String supplierSku;

    @Column(name = "supplier_name", length = 255)
    private String supplierName;

    @Column(name = "supplier_product_url", length = 1000)
    private String supplierProductUrl;

    @Embedded
    private LogisticsEmbeddable logistica;

    @Column(name = "is_dropship", nullable = false)
    private boolean dropship = true;

    @Embedded
    private StockEmbeddable estoque;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "brand", length = 255)
    private String brand;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "compare_at_price", precision = 19, scale = 4)
    private BigDecimal compareAtPrice;

    @Embedded
    private CommercialEmbeddable comercial;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributes;

    @Column(name = "ean", length = 20)
    private String ean;

    @Column(name = "is_ean_interno", nullable = false)
    private boolean isEanInterno = false;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
