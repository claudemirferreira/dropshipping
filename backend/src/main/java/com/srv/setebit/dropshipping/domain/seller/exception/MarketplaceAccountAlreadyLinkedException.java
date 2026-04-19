package com.srv.setebit.dropshipping.domain.seller.exception;

public class MarketplaceAccountAlreadyLinkedException extends RuntimeException {

    public MarketplaceAccountAlreadyLinkedException(Long marketplaceUserId) {
        super("A conta do marketplace (id=" + marketplaceUserId + ") já está vinculada a outro usuário.");
    }
}
