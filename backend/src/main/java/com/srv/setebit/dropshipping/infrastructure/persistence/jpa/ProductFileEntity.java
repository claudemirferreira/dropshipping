package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import com.srv.setebit.dropshipping.domain.product.ProductFileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFileEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private ProductFileType fileType;

    @Column(name = "object_name", nullable = false, length = 1000)
    private String objectName;

    @Column(name = "original_name", length = 500)
    private String originalName;

    @Column(name = "position", nullable = false)
    private int position = 0;

    @Column(name = "is_main", nullable = false)
    private boolean main = false;

    @Column(name = "alt_text", length = 500)
    private String altText;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

