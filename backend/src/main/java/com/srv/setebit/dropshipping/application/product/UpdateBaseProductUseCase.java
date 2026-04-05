package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.request.UpdateProductRequestDTO;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductResponseDTO;
import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.exception.DuplicateSkuException;
import com.srv.setebit.dropshipping.domain.product.exception.DuplicateSlugException;
import com.srv.setebit.dropshipping.domain.product.exception.InvalidStockException;
import com.srv.setebit.dropshipping.domain.product.exception.InvalidValueException;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
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
public class UpdateBaseProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateBaseProductUseCase.class);
    private final ProductRepositoryPort productRepository;

    public UpdateBaseProductUseCase(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponseDTO execute(UUID id, UpdateProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Valida SKU único (exceto o próprio produto)
        if (productRepository.existsBySkuAndIdNot(request.sku(), id)) {
            throw new DuplicateSkuException(request.sku());
        }

        String slug = (request.slug() == null || request.slug().isBlank())
                ? toSlug(request.nome())
                : toSlug(request.slug());

        if (productRepository.existsBySlugAndIdNot(slug, id)) {
            throw new DuplicateSlugException(slug);
        }

        // Validação de estoque
        if (request.estoque() != null && request.logistica() != null) {
            if (request.estoque().minimo() > request.estoque().atual()) {
                throw new InvalidStockException();
            }
            validatePositive(request);
        }

        // EAN
        String ean = product.getEan();
        boolean eanInterno = product.isEanInterno();
        if (request.codigos() != null) {
            if (Boolean.TRUE.equals(request.codigos().isEanInterno())) {
                eanInterno = true;
                if (request.codigos().ean() == null || request.codigos().ean().isBlank()) {
                    ean = generateUniqueEan();
                } else {
                    ean = request.codigos().ean();
                }
            } else {
                ean = request.codigos().ean();
                eanInterno = false;
            }
        }

        // Atualiza campos
        product.setName(request.nome().trim());
        product.setSlug(slug);
        product.setSku(request.sku().trim());
        product.setCategoryId(parseUuidOrNull(request.categoriaId()));
        product.setBrand(request.marca());
        product.setShortDescription(request.descricaoCurta().trim());
        product.setFullDescription(request.descricaoCompleta());

        if (request.logistica() != null) {
            product.setWeight(request.logistica().pesoKg());
            product.setLength(request.logistica().comprimentoCm());
            product.setWidth(request.logistica().larguraCm());
            product.setHeight(request.logistica().alturaCm());
            product.setLeadTimeDays(request.logistica().leadTimeEnvioDias());
        }

        if (request.estoque() != null) {
            product.setStockQuantity(request.estoque().atual());
            product.setStockMinimum(request.estoque().minimo());
        }

        if (request.comercial() != null) {
            log.info("Atualizando comercial: custo={}, venda={}", request.comercial().valorCusto(), request.comercial().valorVenda());
            product.setCostPrice(request.comercial().valorCusto());
            product.setSalePrice(request.comercial().valorVenda());
            product.setSellerFeePercent(request.comercial().percentualTaxaSeller() != null
                    ? request.comercial().percentualTaxaSeller()
                    : java.math.BigDecimal.valueOf(5.0));
            product.setWarranty(request.comercial().garantia());
        }

        product.setEan(ean);
        product.setEanInterno(eanInterno);
        product.setTags(request.tags() != null ? String.join(",", request.tags()) : null);
        product.setUpdatedAt(Instant.now());

        product = productRepository.save(product);
        log.info("Produto base atualizado com id={}, salePrice={}, costPrice={}", product.getId(), product.getSalePrice(), product.getCostPrice());
        return new ProductResponseDTO(
                product.getId(), product.getName(), product.getSlug(), product.getSku(),
                product.getStatus(), product.getEan(), product.isEanInterno(), product.getCreatedAt()
        );
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

    private void validatePositive(UpdateProductRequestDTO request) {
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
