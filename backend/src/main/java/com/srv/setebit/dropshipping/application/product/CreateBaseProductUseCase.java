package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.request.ProductRequestDTO;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductResponseDTO;
import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import com.srv.setebit.dropshipping.domain.product.exception.DuplicateSkuException;
import com.srv.setebit.dropshipping.domain.product.exception.DuplicateSlugException;
import com.srv.setebit.dropshipping.domain.product.exception.InvalidStockException;
import com.srv.setebit.dropshipping.domain.product.exception.InvalidValueException;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CreateBaseProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateBaseProductUseCase.class);
    private static final String DEFAULT_CURRENCY = "BRL";
    private final ProductRepositoryPort productRepository;

    public CreateBaseProductUseCase(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponseDTO execute(UUID createdBy, ProductRequestDTO request) {
        String slug = (request.slug() == null || request.slug().isBlank())
                ? toSlug(request.nome())
                : toSlug(request.slug());

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        if (productRepository.existsBySlug(slug)) {
            throw new DuplicateSlugException(slug);
        }

        if (request.estoque().minimo() > request.estoque().atual()) {
            throw new InvalidStockException();
        }

        validatePositive(request);

        String ean = null;
        boolean eanInterno = false;
        if (request.codigos() != null) {
            if (Boolean.TRUE.equals(request.codigos().isEanInterno())) {
                eanInterno = true;
                ean = (request.codigos().ean() == null || request.codigos().ean().isBlank())
                        ? generateUniqueEan()
                        : request.codigos().ean();
            } else {
                ean = request.codigos().ean();
            }
        }

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setSku(request.sku().trim());
        product.setName(request.nome().trim());
        product.setShortDescription(request.descricaoCurta().trim());
        product.setFullDescription(request.descricaoCompleta());
        product.setCostPrice(request.comercial().valorCusto());
        product.setSalePrice(request.comercial().valorVenda());
        product.setCurrency(DEFAULT_CURRENCY);
        product.setStatus(ProductStatus.DRAFT);
        product.setSupplierSku(null);
        product.setSupplierName(null);
        product.setSupplierProductUrl(null);
        product.setLeadTimeDays(request.logistica().leadTimeEnvioDias());
        product.setDropship(true);
        product.setWeight(request.logistica().pesoKg());
        product.setLength(request.logistica().comprimentoCm());
        product.setWidth(request.logistica().larguraCm());
        product.setHeight(request.logistica().alturaCm());
        product.setSlug(slug);
        product.setCategoryId(parseUuidOrNull(request.categoriaId()));
        product.setBrand(request.marca());
        product.setMetaTitle(null);
        product.setMetaDescription(null);
        product.setCompareAtPrice(null);
        product.setStockQuantity(request.estoque().atual());
        product.setTags(request.tags() != null ? String.join(",", request.tags()) : null);
        product.setAttributes(null);
        product.setEan(ean);
        product.setEanInterno(eanInterno);
        product.setStockMinimum(request.estoque().minimo());
        product.setSellerFeePercent(request.comercial().percentualTaxaSeller() != null
                ? request.comercial().percentualTaxaSeller()
                : java.math.BigDecimal.valueOf(5.0));
        product.setWarranty(request.comercial().garantia());
        product.setCreatedBy(createdBy);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        product = productRepository.save(product);
        log.info("Produto base criado com id={}", product.getId());
        return new ProductResponseDTO(product.getId(), product.getName(), product.getSlug(), product.getSku(),
                product.getStatus(), product.getEan(), product.isEanInterno(), product.getCreatedAt());
    }

    private java.util.UUID parseUuidOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return java.util.UUID.fromString(value.trim());
        } catch (IllegalArgumentException ex) {
            log.warn("Categoria informada não é um UUID válido: {}", value);
            return null;
        }
    }

    private void validatePositive(ProductRequestDTO request) {
        if (request.logistica().pesoKg().signum() <= 0
                || request.logistica().alturaCm().signum() <= 0
                || request.logistica().larguraCm().signum() <= 0
                || request.logistica().comprimentoCm().signum() <= 0
                || request.logistica().leadTimeEnvioDias() <= 0
                || request.comercial().valorCusto().signum() <= 0) {
            throw new InvalidValueException("Valores de logística e custo devem ser positivos");
        }
    }

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-").replaceAll("-{2,}", "-");
    }

    private String generateUniqueEan() {
        String ean;
        int attempts = 0;
        do {
            ean = generateEan13();
            attempts++;
        } while (productRepository.existsByEan(ean) && attempts < 10);
        return ean;
    }

    private String generateEan13() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            int d = ThreadLocalRandom.current().nextInt(0, 10);
            sb.append(d);
        }
        int checksum = ean13Checksum(sb.toString());
        sb.append(checksum);
        return sb.toString();
    }

    private int ean13Checksum(String twelveDigits) {
        int sum = 0;
        for (int i = 0; i < twelveDigits.length(); i++) {
            int digit = twelveDigits.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int mod = sum % 10;
        return mod == 0 ? 0 : 10 - mod;
    }
}

