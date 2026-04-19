package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import com.srv.setebit.dropshipping.domain.seller.MarketplaceTokenResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.exception.MarketplaceAccountAlreadyLinkedException;
import com.srv.setebit.dropshipping.domain.seller.port.MarketplaceAuthPort;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Orquestra o fluxo de conexão de um usuário a um marketplace:
 * 1. Troca o authorization code por tokens via API do marketplace
 * 2. Valida que a conta do marketplace não está vinculada a outro usuário
 * 3. Faz upsert na tabela seller (insere ou atualiza)
 */
@Service
public class ConnectMarketplaceUseCase {

    private final MarketplaceAuthPort marketplaceAuthPort;
    private final SellerRepositoryPort sellerRepository;

    public ConnectMarketplaceUseCase(MarketplaceAuthPort marketplaceAuthPort,
                                     SellerRepositoryPort sellerRepository) {
        this.marketplaceAuthPort = marketplaceAuthPort;
        this.sellerRepository = sellerRepository;
    }

    @Transactional
    public Seller execute(UUID userId, MarketplaceEnum marketplace, String code) {
        MarketplaceTokenResponse tokenResponse = marketplaceAuthPort.exchangeCode(code);

        validarContaNaoVinculadaAOutroUsuario(tokenResponse.marketplaceUserId(), userId);

        return sellerRepository
                .findByUserIdAndMarketplace(userId, marketplace)
                .map(existing -> atualizarSeller(existing, tokenResponse))
                .orElseGet(() -> criarSeller(userId, marketplace, tokenResponse));
    }

    private void validarContaNaoVinculadaAOutroUsuario(Long marketplaceUserId, UUID userId) {
        if (sellerRepository.existsByMarketplaceUserId(marketplaceUserId, userId)) {
            throw new MarketplaceAccountAlreadyLinkedException(marketplaceUserId);
        }
    }

    private Seller atualizarSeller(Seller existing, MarketplaceTokenResponse tokenResponse) {
        existing.applyTokenResponse(tokenResponse);
        return sellerRepository.save(existing);
    }

    private Seller criarSeller(UUID userId, MarketplaceEnum marketplace, MarketplaceTokenResponse tokenResponse) {
        Seller seller = Seller.builder()
                .userId(userId)
                .marketplace(marketplace)
                .marketplaceId(tokenResponse.marketplaceUserId())
                .build();
        seller.registerForUser();
        seller.applyTokenResponse(tokenResponse);
        return sellerRepository.save(seller);
    }
}
