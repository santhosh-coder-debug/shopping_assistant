package com.ecommerce.assistant.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

@Configuration
public class SecurityConfig {

 @Bean
 SecurityFilterChain security(
         HttpSecurity http,
         @Value("${app.admin-api-key}") String adminKey) throws Exception {
  if (adminKey.isBlank()) {
   throw new IllegalStateException("ADMIN_API_KEY must not be blank");
  }

  http.csrf(csrf -> csrf.disable())
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(authorize -> authorize
                  .requestMatchers("/actuator/health/**").permitAll()
                  .requestMatchers(HttpMethod.GET, "/actuator/prometheus").permitAll()
                  .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                  .requestMatchers(HttpMethod.POST, "/api/v1/chat").permitAll()
                  .requestMatchers(HttpMethod.POST, "/api/v1/products", "/api/v1/products/**").permitAll()
                  .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").permitAll()
                  .anyRequest().denyAll())
          .addFilterBefore(new OncePerRequestFilter() {
           @Override
           protected void doFilterInternal(
                   HttpServletRequest request,
                   HttpServletResponse response,
                   FilterChain chain) throws ServletException, IOException {
            String servletPath = request.getRequestURI()
                    .substring(request.getContextPath().length());
            boolean productWrite = servletPath.equals("/api/v1/products")
                    || servletPath.startsWith("/api/v1/products/");
            boolean writeMethod = HttpMethod.POST.matches(request.getMethod())
                    || HttpMethod.PUT.matches(request.getMethod())
                    || HttpMethod.DELETE.matches(request.getMethod());

            if (productWrite && writeMethod) {
             byte[] expected = adminKey.getBytes(StandardCharsets.UTF_8);
             byte[] supplied = Objects.toString(request.getHeader("X-Admin-Key"), "")
                     .getBytes(StandardCharsets.UTF_8);
             if (!MessageDigest.isEqual(expected, supplied)) {
              response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
              return;
             }
            }
            chain.doFilter(request, response);
           }
          }, BasicAuthenticationFilter.class);
  return http.build();
 }
}
