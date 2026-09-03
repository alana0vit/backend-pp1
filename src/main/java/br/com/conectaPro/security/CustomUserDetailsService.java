package br.com.conectaPro.security;

import br.com.conectaPro.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository repository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository
        .findByEmail(username)
        .map(CustomUserDetails::new)
        .orElseThrow(
            () -> new UsernameNotFoundException("Usuário não encontrado com o email: " + username));
  }
}
