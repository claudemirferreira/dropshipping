package com.srv.setebit.dropshipping.domain.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    private UUID id;
    private UUID productId;
    private String url;
    private int position;
    private boolean main;
    private String altText;
}
