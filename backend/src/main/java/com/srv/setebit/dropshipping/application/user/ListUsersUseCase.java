package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.user.dto.response.PageUserResponse;
import com.srv.setebit.dropshipping.application.user.dto.response.UserResponse;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListUsersUseCase {

    private final UserRepositoryPort userRepository;
    private final GetUserPerfisUseCase getUserPerfisUseCase;

    public ListUsersUseCase(UserRepositoryPort userRepository, GetUserPerfisUseCase getUserPerfisUseCase) {
        this.userRepository = userRepository;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
    }

    public PageUserResponse execute(String name, String email, String perfilCode, Pageable pageable) {
        Page<User> users = userRepository.findAllByFilter(
                name != null && !name.isBlank() ? name.trim() : null,
                email != null && !email.isBlank() ? email.trim().toLowerCase() : null,
                perfilCode != null && !perfilCode.isBlank() ? perfilCode.trim() : null,
                pageable
        );
        List<UserResponse> content = users.getContent().stream()
                .map(user -> toResponse(user, getUserPerfisUseCase.execute(user.getId()).stream().map(p -> p.code()).collect(Collectors.toList())))
                .toList();
        return new PageUserResponse(
                content,
                users.getTotalElements(),
                users.getTotalPages(),
                users.getSize(),
                users.getNumber(),
                users.isFirst(),
                users.isLast()
        );
    }

    private UserResponse toResponse(User user, List<String> perfilCodes) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.isActive(),
                perfilCodes,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
