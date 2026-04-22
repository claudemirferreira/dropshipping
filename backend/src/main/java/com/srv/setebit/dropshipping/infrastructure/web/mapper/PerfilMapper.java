package com.srv.setebit.dropshipping.infrastructure.web.mapper;

import com.srv.setebit.dropshipping.application.access.dto.request.UpdatePerfilRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import com.srv.setebit.dropshipping.domain.access.Perfil;
import org.springframework.stereotype.Component;

@Component
public class PerfilMapper {

    public PerfilResponse toPerfilResponse(Perfil perfil) {
        return PerfilResponse
                .builder()
                .id(perfil.getId())
                .createdAt(perfil.getCreatedAt())
                .code(perfil.getCode())
                .icon(perfil.getIcon())
                .active(perfil.isActive())
                .displayOrder(perfil.getDisplayOrder())
                .updatedAt(perfil.getUpdatedAt())
                .name(perfil.getName())
                .build();
    }

    public Perfil toPerfil(UpdatePerfilRequest updatePerfilRequest) {
        return Perfil
                .builder()
                .id(updatePerfilRequest.id())
                .code(updatePerfilRequest.code())
                .icon(updatePerfilRequest.icon())
                .active(updatePerfilRequest.active())
                .name(updatePerfilRequest.name())
                .build();
    }

}
