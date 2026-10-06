package com.prepforge.config;
import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
  http.csrf(c->c.disable()).authorizeHttpRequests(a->a
   .requestMatchers("/","/index.html","/student-login.html","/student-register.html","/admin-login.html","/css/**","/js/**","/api/auth/**","/api/public/**","/student/**","/companies.html").permitAll()
   .requestMatchers("/api/admin/**","/admin/**").hasRole("ADMIN")
   .requestMatchers("/api/student/**").hasRole("STUDENT").anyRequest().permitAll())
   .formLogin(f->f.disable()).logout(l->l.logoutUrl("/api/auth/logout").logoutSuccessHandler((req,res,auth)->res.setStatus(200)))
   .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->res.sendError(401)));
  return http.build();
 }
}
