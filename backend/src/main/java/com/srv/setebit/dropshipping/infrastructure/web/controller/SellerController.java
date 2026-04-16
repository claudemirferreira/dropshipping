package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.seller.CreateSellerUseCase;
import com.srv.setebit.dropshipping.application.seller.DeleteSellerUseCase;
import com.srv.setebit.dropshipping.application.seller.GetSellerByIdUseCase;
import com.srv.setebit.dropshipping.application.seller.ListSellersUseCase;
import com.srv.setebit.dropshipping.application.seller.UpdateSellerUseCase;
import com.srv.setebit.dropshipping.application.seller.dto.request.UpdateSellerRequest;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.infrastructure.web.dto.seller.CreateSellerRequest;
import com.srv.setebit.dropshipping.infrastructure.web.mapper.SellerMapper;
import com.srv.setebit.dropshipping.application.seller.dto.response.PageSellerResponse;
import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ml-sellers")
@Tag(name = "Mercado Livre — Seller", description = "CRUD de tokens OAuth do vendedor (id = usuário da aplicação)")
public class SellerController {

    private static final Set<String> SELLER_SORT_PROPERTIES = Set.of("id", "createdAt", "updatedAt", "marketplaceId");

    private final ListSellersUseCase listSellersUseCase;
    private final CreateSellerUseCase createSellerUseCase;
    private final GetSellerByIdUseCase getSellerByIdUseCase;
    private final UpdateSellerUseCase updateSellerUseCase;
    private final DeleteSellerUseCase deleteSellerUseCase;
    private final SellerMapper sellerWebMapper;

    public SellerController(ListSellersUseCase listSellersUseCase,
                            CreateSellerUseCase createSellerUseCase,
                            GetSellerByIdUseCase getSellerByIdUseCase,
                            UpdateSellerUseCase updateSellerUseCase,
                            DeleteSellerUseCase deleteSellerUseCase,
                            SellerMapper sellerWebMapper) {
        this.listSellersUseCase = listSellersUseCase;
        this.createSellerUseCase = createSellerUseCase;
        this.getSellerByIdUseCase = getSellerByIdUseCase;
        this.updateSellerUseCase = updateSellerUseCase;
        this.deleteSellerUseCase = deleteSellerUseCase;
        this.sellerWebMapper = sellerWebMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar sellers Mercado Livre")
    public ResponseEntity<PageSellerResponse> list(
            @Parameter(description = "Filtro opcional por marketplace_id (ID da conta no marketplace)")
            @RequestParam(required = false) Long marketplaceId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable safePageable = sanitizeSort(pageable, SELLER_SORT_PROPERTIES, "createdAt");
        PageSellerResponse response = listSellersUseCase.execute(marketplaceId, safePageable);
        return ResponseEntity.ok(response);
    }

    private Pageable sanitizeSort(Pageable pageable, Set<String> allowedProperties, String defaultProperty) {
        Sort sort = pageable.getSort().stream()
                .filter(order -> allowedProperties.contains(order.getProperty()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Sort::by));
        if (sort.isEmpty()) {
            sort = Sort.by(Sort.Direction.DESC, defaultProperty);
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #request.userId == authentication.principal")
    @Operation(summary = "Criar registro de seller (um por usuário)")
    public ResponseEntity<SellerResponse> create(@Valid @RequestBody CreateSellerRequest request) {
        Seller seller = createSellerUseCase.execute(sellerWebMapper.toCreate(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerWebMapper.toResponse(seller));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #id == authentication.principal")
    @Operation(summary = "Buscar seller por ID do usuário")
    public ResponseEntity<SellerResponse> getById(@PathVariable UUID id) {
        SellerResponse response = getSellerByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #id == authentication.principal")
    @Operation(summary = "Atualizar tokens do seller")
    public ResponseEntity<SellerResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateSellerRequest request) {
        SellerResponse response = updateSellerUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #id == authentication.principal")
    @Operation(summary = "Excluir registro de seller")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteSellerUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
