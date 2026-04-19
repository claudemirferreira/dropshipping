package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.seller.ConnectMarketplaceUseCase;
import com.srv.setebit.dropshipping.application.seller.GetMarketplaceAuthUrlUseCase;
import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.infrastructure.web.dto.marketplace.MarketplaceAuthUrlResponse;
import com.srv.setebit.dropshipping.infrastructure.web.mapper.SellerMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/marketplace")
@Tag(name = "Marketplace — Autenticação", description = "Conexão OAuth2 com marketplaces (Mercado Livre, Shopee, etc.)")
public class MarketplaceController {

    private final GetMarketplaceAuthUrlUseCase getAuthUrlUseCase;
    private final ConnectMarketplaceUseCase connectUseCase;
    private final SellerMapper sellerMapper;
    private final String frontendBaseUrl;

    public MarketplaceController(GetMarketplaceAuthUrlUseCase getAuthUrlUseCase,
            ConnectMarketplaceUseCase connectUseCase,
            SellerMapper sellerMapper,
            @Value("${app.frontend-base-url:http://localhost:4200}") String frontendBaseUrl) {
        this.getAuthUrlUseCase = getAuthUrlUseCase;
        this.connectUseCase = connectUseCase;
        this.sellerMapper = sellerMapper;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @GetMapping("/auth-url")
    @Operation(summary = "Obter URL de autorização", description = "Retorna a URL para redirecionar o usuário ao login do marketplace. "
            +
            "O frontend redireciona o usuário e, após autorização, " +
            "recebe o 'code' na redirect_uri para chamar /connect.")
    public ResponseEntity<MarketplaceAuthUrlResponse> getAuthUrl(
            @RequestParam MarketplaceEnum marketplace,
            @AuthenticationPrincipal UUID userId) {
        String authUrl = getAuthUrlUseCase.execute(marketplace, userId);
        return ResponseEntity.ok(new MarketplaceAuthUrlResponse(authUrl));
    }

    @GetMapping("/callback")
    @Operation(summary = "Callback do Marketplace (Retorno Automático)", description = "Endpoint que recebe o redirecionamento oficial do Mercado Livre com o 'code' e 'state'."
            +
            "A requisição autentica o code, salva na base de dados, e emite um redirect(302) para o frontend.")
    public ResponseEntity<Void> callback(
            @RequestParam String code,
            @RequestParam String state) {

        connectUseCase.execute(state, code);

        URI redirectUrl = URI.create(frontendBaseUrl + "/integrations?status=success");
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUrl).build();
    }
}
