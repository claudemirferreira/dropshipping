package com.srv.setebit.dropshipping.infrastructure.client;

import com.srv.setebit.dropshipping.infrastructure.mercadolivre.MercadoLivreTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client para a API OAuth2 do Mercado Livre.
 * Tanto a troca de código quanto o refresh utilizam o mesmo endpoint;
 * apenas o campo {@code grant_type} no body difere entre as duas operações.
 */
@FeignClient(
        name = "mercadolivre-auth",
        url = "${mercadolivre.base-url}"
)
public interface MercadoLivreClient {

    @PostMapping(
            value = "/oauth/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    MercadoLivreTokenResponse exchangeToken(
            @RequestBody MultiValueMap<String, String> body
    );
}
