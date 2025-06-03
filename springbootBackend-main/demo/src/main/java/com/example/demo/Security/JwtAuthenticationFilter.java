package com.example.demo.Security;

import com.example.demo.utils.JwtTokenUtil;
import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.MyAppUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MyAppUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Bỏ "Bearer "
            try {
                String email = jwtTokenUtil.extractEmail(token);
                Optional<MyAppUser> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    MyAppUser user = userOpt.get();

                    String roleName = user.getRole().getName(); // ví dụ: "ADMIN"
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.singletonList(authority));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token lỗi → không set authentication
            }
        }

        filterChain.doFilter(request, response);
    }
}
