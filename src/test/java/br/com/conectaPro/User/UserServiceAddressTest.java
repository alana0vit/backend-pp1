package br.com.conectaPro.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.conectaPro.dto.CoordinatesDTO;
import br.com.conectaPro.model.category.CategoryRepository;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.AddressUserRepository;
import br.com.conectaPro.model.user.UserRepository;
import br.com.conectaPro.model.user.UserService;
import br.com.conectaPro.util.GeoLocationService;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceAddressTest {

  @Mock private UserRepository userRepository;
  @Mock private AddressUserRepository addressUserRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private GeoLocationService geoLocationService;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private UserService userService;

  private AddressUser enderecoNovo() {
    AddressUser novo = new AddressUser();
    novo.setStreet("Rua Inventada");
    novo.setNumber("10");
    novo.setNeighborhood("Centro");
    novo.setCity("Recife");
    novo.setState("PE");
    novo.setZipCode("50000-000");
    return novo;
  }

  @Test
  @DisplayName("Editar endereço não falha se o geocoding falhar; coordenadas antigas são limpas")
  void atualizaEnderecoMesmoSemGeocoding() {
    AddressUser existente = new AddressUser();
    existente.setId(1L);
    existente.setLatitude(-8.0);
    existente.setLongitude(-34.9);

    when(addressUserRepository.findById(1L)).thenReturn(Optional.of(existente));
    when(geoLocationService.getCoordinates(any(AddressUser.class)))
        .thenThrow(new RuntimeException("Nenhuma coordenada encontrada"));
    when(addressUserRepository.save(any(AddressUser.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AddressUser resultado = userService.updateAddressUser(1L, enderecoNovo());

    assertEquals("Rua Inventada", resultado.getStreet());
    assertNull(resultado.getLatitude());
    assertNull(resultado.getLongitude());
  }

  @Test
  @DisplayName("Editar endereço grava as novas coordenadas quando o geocoding funciona")
  void atualizaEnderecoComGeocoding() {
    AddressUser existente = new AddressUser();
    existente.setId(1L);

    CoordinatesDTO coords = new CoordinatesDTO();
    coords.setLat("-8.0476");
    coords.setLon("-34.877");

    when(addressUserRepository.findById(1L)).thenReturn(Optional.of(existente));
    when(geoLocationService.getCoordinates(any(AddressUser.class))).thenReturn(coords);
    when(addressUserRepository.save(any(AddressUser.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AddressUser resultado = userService.updateAddressUser(1L, enderecoNovo());

    assertEquals(-8.0476, resultado.getLatitude());
    assertEquals(-34.877, resultado.getLongitude());
  }

  @Test
  @DisplayName("Endereço inexistente lança NoSuchElementException (vira 404)")
  void enderecoInexistenteLancaNaoEncontrado() {
    when(addressUserRepository.findById(99L)).thenReturn(Optional.empty());

    NoSuchElementException exception =
        assertThrows(
            NoSuchElementException.class,
            () -> userService.updateAddressUser(99L, enderecoNovo()));

    assertEquals("Endereço não encontrado com o ID: 99", exception.getMessage());
  }

  @Test
  @DisplayName("Usuário inexistente lança NoSuchElementException (vira 404)")
  void usuarioInexistenteLancaNaoEncontrado() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    NoSuchElementException exception =
        assertThrows(NoSuchElementException.class, () -> userService.getById(99L));

    assertEquals("Usuário não encontrado com o ID: 99", exception.getMessage());
  }
}
