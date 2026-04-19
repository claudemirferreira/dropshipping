package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.seller.ConnectMarketplaceUseCase;
import com.srv.setebit.dropshipping.application.seller.GetMarketplaceAuthUrlUseCase;
import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.infrastructure.web.dto.marketplace.ConnectMarketplaceRequest;
import com.srv.setebit.dropshipping.infrastructure.web.dto.marketplace.MarketplaceAuthUrlResponse;
import com.srv.setebit.dropshipping.infrastructure.web.mapper.SellerMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/marketplace")
@Tag(name = "Marketplace — Autenticação", description = "Conexão OAuth2 com marketplaces (Mercado Livre, Shopee, etc.)")
public class MarketplaceController {

    private final GetMarketplaceAuthUrlUseCase getAuthUrlUseCase;
    private final ConnectMarketplaceUseCase connectUseCase;
    private final SellerMapper sellerMapper;

    public MarketplaceController(GetMarketplaceAuthUrlUseCase getAuthUrlUseCase,
                                 ConnectMarketplaceUseCase connectUseCase,
                                 SellerMapper sellerMapper) {
        this.getAuthUrlUseCase = getAuthUrlUseCase;
        this.connectUseCase = connectUseCase;
        this.sellerMapper = sellerMapper;
    }

    @GetMapping("/auth-url")
    @Operation(
            summary = "Obter URL de autorização",
            description = "Retorna a URL para redirecionar o usuário ao login do marketplace. " +
                          "O frontend redireciona o usuário e, após autorização, " +
                          "recebe o 'code' na redirect_uri para chamar /connect."
    )
    public ResponseEntity<MarketplaceAuthUrlResponse> getAuthUrl(
            @RequestParam MarketplaceEnum marketplace,
            @AuthenticationPrincipal UUID userId) {
        String authUrl = getAuthUrlUseCase.execute(marketplace);
        return ResponseEntity.ok(new MarketplaceAuthUrlResponse(authUrl));
    }

    @PostMapping("/connect")
    @Operation(
            summary = "Conectar ao marketplace",
            description = "Troca o authorization code (obtido após o usuário autorizar) por tokens OAuth2 " +
                          "e salva/atualiza as credenciais do seller. " +
                          "O userId é extraído automaticamente do JWT."
    )
    public ResponseEntity<SellerResponse> connect(
            @Valid @RequestBody ConnectMarketplaceRequest request,
            @AuthenticationPrincipal UUID userId) {
        Seller seller = connectUseCase.execute(userId, request.marketplace(), request.code());
        return ResponseEntity.ok(sellerMapper.toResponse(seller));
    }
}
