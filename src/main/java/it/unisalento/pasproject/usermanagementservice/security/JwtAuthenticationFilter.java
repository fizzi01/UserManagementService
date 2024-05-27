package it.unisalento.pasproject.usermanagementservice.security;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.exceptions.AccessDeniedException;
import it.unisalento.pasproject.usermanagementservice.exceptions.UserNotAuthorizedException;
import it.unisalento.pasproject.usermanagementservice.service.UserCheckService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private UserCheckService customerUserDetailsService ;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, UserNotAuthorizedException, AccessDeniedException {

        final String authorizationHeader = request.getHeader("Authorization");

        LOGGER.info("Authorization header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        try {
            LOGGER.info("Extracting username from JWT token");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtilities.extractUsername(jwt);
            } else {
                LOGGER.info("Missing token");
                throw new AccessDeniedException("Missing token");
            }
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            LOGGER.info("Checking user details");
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

        if ( SecurityContextHolder.getContext().getAuthentication() == null ) {
            throw new AccessDeniedException("No authentication found");
        }

        chain.doFilter(request, response);
    }

}