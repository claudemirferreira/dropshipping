package com.srv.setebit.dropshipping.domain.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFile {

    private UUID id;
    private UUID productId;
    private ProductFileType fileType;
    /**
     * Nome do objeto no bucket (ex: imagem/{productId}/{uuid}.jpg ou video/{productId}/{uuid}.mp4)
     */
    private String objectName;
    private String originalName;
    private int position;
    private boolean main;
    private String altText;
    private Instant createdAt;
}

