package com.srv.setebit.dropshipping.infrastructure.security;

import com.srv.setebit.dropshipping.domain.access.port.UserPerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.UserProfile;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProviderAdapter jwtProvider;
    private final UserRepositoryPort userRepository;
    private final UserPerfilRepositoryPort userPerfilRepository;

    public JwtAuthenticationFilter(JwtProviderAdapter jwtProvider,
                                   UserRepositoryPort userRepository,
                                   UserPerfilRepositoryPort userPerfilRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.userPerfilRepository = userPerfilRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractJwt(request);

            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                UUID userId = jwtProvider.getUserIdFromToken(jwt);
                Optional<User> userOpt = userRepository.findById(userId);

                if (userOpt.isPresent() && userOpt.get().isActive()) {
                    User user = userOpt.get();
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getProfile().name()));
                    List<String> rotinaCodes = userPerfilRepository.findRotinaCodesByUserId(user.getId());
                    for (String code : rotinaCodes) {
                        authorities.add(new SimpleGrantedAuthority(code));
                    }

                    var authentication = new UsernamePasswordAuthenticationToken(
                            user.getId(),
                            null,
                            authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Invalid token - continue without authentication
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}
