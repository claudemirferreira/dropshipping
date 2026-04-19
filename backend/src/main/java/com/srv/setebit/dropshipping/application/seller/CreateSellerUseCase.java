package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.exception.SellerAlreadyExistsException;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.exception.UserNotFoundException;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSellerUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final UserRepositoryPort userRepository;

    public CreateSellerUseCase(SellerRepositoryPort sellerRepository, UserRepositoryPort userRepository) {
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Seller execute(Seller seller) {
        validate(seller);
        seller.registerForUser();
        return sellerRepository.save(seller);
    }

    private void validate(Seller seller) {
        seller.validate();
        userRepository.findById(seller.getUserId()).orElseThrow(() -> new UserNotFoundException(seller.getUserId()));
        if (sellerRepository.existsById(seller.getUserId())) {
            throw new SellerAlreadyExistsException(seller.getUserId());
        }
    }

}
