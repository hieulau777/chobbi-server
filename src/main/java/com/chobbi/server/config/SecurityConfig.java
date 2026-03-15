package com.chobbi.server.config;

import com.chobbi.server.auth.AdminPasswordFilter;
import com.chobbi.server.auth.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminPasswordFilter adminPasswordFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AdminPasswordFilter adminPasswordFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.adminPasswordFilter = adminPasswordFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers("/ws", "/ws/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/address/provinces/**", "/address/districts/**").permitAll();
                    req.requestMatchers("/profile/info", "/profile/address/**").authenticated();
                    req.requestMatchers("/cart/**").authenticated();
                    req.requestMatchers("/notification/**").authenticated();
                    req.requestMatchers("/shipping/**").permitAll();

                    // API public: trang chủ + category + product list + product detail client + product search (client không đăng nhập)
                    req.requestMatchers(HttpMethod.GET, "/category/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/product/list").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/product/client").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/product/search").permitAll();
                    // API public: danh sách thương hiệu cho nhánh category (dùng cho client)
                    req.requestMatchers(HttpMethod.GET, "/product/brands").permitAll();
                    // API public trang shop
                    req.requestMatchers(HttpMethod.GET, "/shop/public/**").permitAll();
                    // Chỉ SELLER: shop, promotion, product create/update/seller list, order shop/ship/cancel
                    req.requestMatchers("/shop/**").hasRole("SELLER");
                    req.requestMatchers("/promotion/**").hasRole("SELLER");
                    req.requestMatchers(HttpMethod.POST, "/product/create", "/product/update").hasRole("SELLER");
                    req.requestMatchers(HttpMethod.GET, "/product/seller/list").hasRole("SELLER");
                    req.requestMatchers(HttpMethod.GET, "/product/{productId}").hasRole("SELLER");
                    req.requestMatchers(HttpMethod.GET, "/order/shop/orders").hasRole("SELLER");
                    req.requestMatchers(HttpMethod.POST, "/order/ship", "/order/cancel").hasRole("SELLER");

                    req.requestMatchers("/order/**").authenticated();
                    req.anyRequest().permitAll();
                })
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"Vui lòng đăng nhập.\"}");
                        })
                        .accessDeniedHandler((HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"Bạn không có quyền truy cập.\"}");
                        }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(adminPasswordFilter, JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("http://localhost:*");
        // Production: client, seller, admin gọi API
        config.addAllowedOriginPattern("https://chobbi.hieulau.net");
        config.addAllowedOriginPattern("https://seller.chobbi.hieulau.net");
        config.addAllowedOriginPattern("https://admin.chobbi.hieulau.net");
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
