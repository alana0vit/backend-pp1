package br.com.conectaPro.Search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.conectaPro.model.category.CategoryRepository;
import br.com.conectaPro.model.user.AddressUserRepository;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserRepository;
import br.com.conectaPro.model.user.UserService;
import br.com.conectaPro.util.GeoLocationService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceSearchMapTest {

  @Mock private UserRepository userRepository;
  @Mock private AddressUserRepository addressUserRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private GeoLocationService geoLocationService;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private UserService userService;

  @Test
  @DisplayName("latitude sem longitude é recusada")
  void latitudeSemLongitude() {
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.searchForMap(null, null, -8.0, null, null));

    assertEquals("Informe latitude e longitude juntas.", e.getMessage());
    verifyNoInteractions(userRepository);
  }

  @Test
  @DisplayName("radiusKm sem origem é recusado (senão o filtro seria ignorado em silêncio)")
  void raioSemOrigem() {
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.searchForMap(null, null, null, null, 10.0));

    assertEquals("radiusKm exige latitude e longitude.", e.getMessage());
  }

  @Test
  @DisplayName("latitude e longitude fora do intervalo são recusadas")
  void coordenadasForaDoIntervalo() {
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.searchForMap(null, null, 91.0, 0.0, null));
    assertThrows(
        IllegalArgumentException.class,
        () -> userService.searchForMap(null, null, 0.0, -181.0, null));
  }

  @Test
  @DisplayName("raio zero continua recusado")
  void raioZero() {
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.searchForMap(null, null, -8.0, -34.9, 0.0));

    assertEquals("O raio deve ser maior que zero", e.getMessage());
  }

  @Test
  @DisplayName("Parâmetros válidos delegam para a busca no repositório")
  void parametrosValidos() {
    User u = new User();
    u.setId(1L);
    when(userRepository.searchUsers("ana", 2L, -8.0, -34.9, 10.0)).thenReturn(List.of(u));

    List<User> resultado = userService.searchForMap("ana", 2L, -8.0, -34.9, 10.0);

    assertEquals(1, resultado.size());
    verify(userRepository).searchUsers("ana", 2L, -8.0, -34.9, 10.0);
  }

  @Test
  @DisplayName("Sem nenhum filtro também é válido (lista todos)")
  void semFiltros() {
    when(userRepository.searchUsers(null, null, null, null, null)).thenReturn(List.of());

    assertEquals(0, userService.searchForMap(null, null, null, null, null).size());
  }
}
