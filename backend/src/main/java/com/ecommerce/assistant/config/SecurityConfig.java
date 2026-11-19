package com.ecommerce.assistant.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.*;import jakarta.servlet.http.*;
import java.io.IOException;
@Configuration
public class SecurityConfig {
 @Bean SecurityFilterChain security(HttpSecurity http,@Value("${app.admin-api-key}") String adminKey) throws Exception {
  http.csrf(csrf->csrf.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
  .authorizeHttpRequests(a->a.requestMatchers("/actuator/health/**").permitAll().requestMatchers(HttpMethod.GET,"/api/v1/products/**").permitAll().requestMatchers(HttpMethod.POST,"/api/v1/chat").permitAll().anyRequest().permitAll())
  .addFilterBefore(new OncePerRequestFilter(){@Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
   if((req.getMethod().equals("POST")||req.getMethod().equals("PUT")||req.getMethod().equals("DELETE"))&&req.getRequestURI().startsWith("/api/v1/products")){
    if(!java.security.MessageDigest.isEqual(adminKey.getBytes(java.nio.charset.StandardCharsets.UTF_8),java.util.Objects.toString(req.getHeader("X-Admin-Key"),"").getBytes(java.nio.charset.StandardCharsets.UTF_8))){res.sendError(401,"Invalid admin key");return;}
   }chain.doFilter(req,res);
  }},BasicAuthenticationFilter.class);
  return http.build();
 }
}
