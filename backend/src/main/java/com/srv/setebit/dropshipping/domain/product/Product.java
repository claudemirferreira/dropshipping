package com.srv.setebit.dropshipping.domain.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private UUID id;
    private String sku;
    private String name;
    private String shortDescription;
    private String fullDescription;
    private BigDecimal salePrice;
    private BigDecimal costPrice;
    private String currency;
    private ProductStatus status;
    private String supplierSku;
    private String supplierName;
    private String supplierProductUrl;
    private Integer leadTimeDays;
    private boolean isDropship;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String slug;
    private UUID categoryId;
    private String brand;
    private String metaTitle;
    private String metaDescription;
    private BigDecimal compareAtPrice;
    private Integer stockQuantity;
    private String tags;
    private String attributes;
    private Instant createdAt;
    private Instant updatedAt;
}
