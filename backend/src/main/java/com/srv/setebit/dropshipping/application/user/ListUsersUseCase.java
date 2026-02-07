package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.user.dto.response.UserResponse;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListUsersUseCase {

    private final UserRepositoryPort userRepository;

    public ListUsersUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponse> execute(String name, String email, String profile, Pageable pageable) {
        Page<User> users = userRepository.findAllByFilter(
                name != null && !name.isBlank() ? name.trim() : null,
                email != null && !email.isBlank() ? email.trim().toLowerCase() : null,
                profile != null && !profile.isBlank() ? profile : null,
                pageable
        );
        return users.map(this::toResponse);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.isActive(),
                user.getProfile(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
