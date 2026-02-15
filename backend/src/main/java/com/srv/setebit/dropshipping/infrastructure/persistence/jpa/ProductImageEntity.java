package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "position", nullable = false)
    private int position = 0;

    @Column(name = "is_main", nullable = false)
    private boolean main = false;

    @Column(name = "alt_text", length = 500)
    private String altText;
}
