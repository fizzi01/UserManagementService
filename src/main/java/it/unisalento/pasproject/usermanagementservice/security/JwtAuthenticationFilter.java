package it.unisalento.pasproject.usermanagementservice.security;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.exceptions.AccessDeniedException;
import it.unisalento.pasproject.usermanagementservice.exceptions.UserNotAuthorizedException;
import it.unisalento.pasproject.usermanagementservice.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtilities jwtUtilities ;

    @Autowired
    private CustomUserDetailsService customerUserDetailsService ;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtilities.extractUsername(jwt);
            }
        } catch (Exception e) {
            throw new AccessDeniedException("Invalid token");
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            User user = this.customerUserDetailsService.loadUserByUsername(username);
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail()) // Assume email is username
                    .password("") // Password field is not used in JWT authentication
                    .authorities(user.getRole()) // Set roles or authorities from the UserDetailsDTO
                    .build();

            if (jwtUtilities.validateToken(jwt, userDetails) && user.getEnabled()) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
            throw new UserNotAuthorizedException("User not authorized");
        }
        }
        chain.doFilter(request, response);
    }

}