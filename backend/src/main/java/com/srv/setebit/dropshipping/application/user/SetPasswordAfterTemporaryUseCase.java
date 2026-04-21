package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.user.dto.request.NewPasswordRequest;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.InvalidCredentialsException;
import com.srv.setebit.dropshipping.domain.user.exception.UserNotFoundException;
import com.srv.setebit.dropshipping.domain.user.port.RefreshTokenRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.TemporaryPasswordRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class SetPasswordAfterTemporaryUseCase {

  private final UserRepositoryPort userRepository;
  private final PasswordEncoderPort passwordEncoder;
  private final RefreshTokenRepositoryPort refreshTokenRepository;
  private final TemporaryPasswordRepositoryPort tempPasswordRepository;

  public SetPasswordAfterTemporaryUseCase(UserRepositoryPort userRepository,
                                          PasswordEncoderPort passwordEncoder,
                                          RefreshTokenRepositoryPort refreshTokenRepository,
                                          TemporaryPasswordRepositoryPort tempPasswordRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.refreshTokenRepository = refreshTokenRepository;
    this.tempPasswordRepository = tempPasswordRepository;
  }

  @Transactional
  public void execute(UUID userId, NewPasswordRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    var latestTempOpt = tempPasswordRepository.findLatestByUserId(userId);
    if (latestTempOpt.isEmpty() || !latestTempOpt.get().isUsed()
        || latestTempOpt.get().getExpiresAt().isBefore(Instant.now())) {
      throw new InvalidCredentialsException();
    }

    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    user.setUpdatedAt(Instant.now());
    userRepository.save(user);

    refreshTokenRepository.revokeByUserId(userId);
    tempPasswordRepository.deleteByUserId(userId);
  }
}