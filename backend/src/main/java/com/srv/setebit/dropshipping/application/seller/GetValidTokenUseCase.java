package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceTokenResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.port.MarketplaceAuthPort;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Retorna um access_token válido para o seller.
 * Se o token estiver expirado, executa o refresh automaticamente (silent refresh),
 * persiste os novos tokens e retorna o token atualizado.
 */
@Service
public class GetValidTokenUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final MarketplaceAuthPort marketplaceAuthPort;

    public GetValidTokenUseCase(SellerRepositoryPort sellerRepository,
                                MarketplaceAuthPort marketplaceAuthPort) {
        this.sellerRepository = sellerRepository;
        this.marketplaceAuthPort = marketplaceAuthPort;
    }

    @Transactional
    public String execute(UUID userId, UUID sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .filter(s -> s.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Seller não encontrado para o usuário."));

        if (seller.isTokenExpired()) {
            seller = refreshAndPersist(seller);
        }

        return seller.getAccessToken();
    }

    private Seller refreshAndPersist(Seller seller) {
        MarketplaceTokenResponse tokenResponse = marketplaceAuthPort.refreshToken(seller.getRefreshToken());
        seller.applyTokenResponse(tokenResponse);
        return sellerRepository.save(seller);
    }
}
