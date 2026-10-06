package com.prepforge.security;
import com.prepforge.entity.User; import com.prepforge.repository.UserRepository;
import org.springframework.security.core.userdetails.*; import org.springframework.stereotype.Service;
@Service public class CustomUserDetailsService implements UserDetailsService {
 private final UserRepository repo; public CustomUserDetailsService(UserRepository repo){this.repo=repo;}
 public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
  User u=repo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
  return org.springframework.security.core.userdetails.User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole().replace("ROLE_","")).build();
 }
}
