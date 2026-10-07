package br.com.conectaPro.Search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.com.conectaPro.dto.ProfessionalMapDTO;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProfessionalMapDTOTest {

  private AddressUser endereco(Long id, Double lat, Double lon) {
    AddressUser a = new AddressUser();
    a.setId(id);
    a.setStreet("Rua Secreta");
    a.setNumber("123");
    a.setNeighborhood("Boa Viagem");
    a.setCity("Recife");
    a.setState("PE");
    a.setZipCode("51021-000");
    a.setLatitude(lat);
    a.setLongitude(lon);
    return a;
  }

  private User profissional(Long id, String nome, int prioridade, AddressUser... enderecos) {
    User u = new User();
    u.setId(id);
    u.setName(nome);
    u.setEmail(nome + "@teste.com");
    u.setPhone("81999998888");
    u.setRegistryId("52998224725");
    u.setBirthDate(LocalDate.of(1990, 5, 15));
    u.setUserType(UserType.PROFESSIONAL);
    u.setPriorityWeight(prioridade);
    u.setVerified(prioridade > 0);
    u.setAdresses(new ArrayList<>(Arrays.asList(enderecos)));
    return u;
  }

  private List<String> nomesDosCampos(Class<?> tipo) {
    return Arrays.stream(tipo.getRecordComponents()).map(RecordComponent::getName).toList();
  }

  @Test
  @DisplayName("O DTO do mapa não expõe dados pessoais nem endereço completo")
  void naoExpoeDadosSensiveis() {
    List<String> proibidos =
        List.of(
            "email", "phone", "registryId", "birthDate", "password", "userType", "street", "number",
            "zipCode", "supplement");

    List<String> campos = new ArrayList<>(nomesDosCampos(ProfessionalMapDTO.class));
    campos.addAll(nomesDosCampos(ProfessionalMapDTO.LocationDTO.class));

    for (String proibido : proibidos) {
      assertFalse(campos.contains(proibido), "campo sensível exposto: " + proibido);
    }
  }

  @Test
  @DisplayName("Ordena por plano (maior primeiro) e depois por distância")
  void ordenaPorPlanoEDistancia() {
    User perto = profissional(1L, "perto", 0, endereco(11L, 0.0, 0.01));
    User longe = profissional(2L, "longe", 0, endereco(12L, 0.0, 0.05));
    User ouro = profissional(3L, "ouro", 3, endereco(13L, 0.0, 0.5));

    List<ProfessionalMapDTO> resultado =
        ProfessionalMapDTO.fromUsers(List.of(longe, perto, ouro), 0.0, 0.0);

    assertEquals(List.of(3L, 1L, 2L), resultado.stream().map(ProfessionalMapDTO::id).toList());
    assertEquals(1.11, resultado.get(1).distanceKm(), 0.01);
    assertEquals(5.56, resultado.get(2).distanceKm(), 0.01);
  }

  @Test
  @DisplayName("Sem origem não calcula distância e desempata pelo id")
  void semOrigemNaoCalculaDistancia() {
    User b = profissional(2L, "b", 0, endereco(12L, 0.0, 0.05));
    User a = profissional(1L, "a", 0, endereco(11L, 0.0, 0.01));

    List<ProfessionalMapDTO> resultado = ProfessionalMapDTO.fromUsers(List.of(b, a), null, null);

    assertEquals(List.of(1L, 2L), resultado.stream().map(ProfessionalMapDTO::id).toList());
    assertNull(resultado.get(0).distanceKm());
    assertNull(resultado.get(0).locations().get(0).distanceKm());
  }

  @Test
  @DisplayName("Endereço sem coordenadas vem com nulos e o profissional vai para o fim")
  void enderecoSemCoordenadas() {
    User semCoord = profissional(1L, "sem", 0, endereco(11L, null, null));
    User comCoord = profissional(2L, "com", 0, endereco(12L, 0.0, 0.05));

    List<ProfessionalMapDTO> resultado =
        ProfessionalMapDTO.fromUsers(List.of(semCoord, comCoord), 0.0, 0.0);

    assertEquals(List.of(2L, 1L), resultado.stream().map(ProfessionalMapDTO::id).toList());
    ProfessionalMapDTO ultimo = resultado.get(1);
    assertNull(ultimo.distanceKm());
    assertNull(ultimo.locations().get(0).latitude());
    assertNull(ultimo.locations().get(0).longitude());
  }

  @Test
  @DisplayName("Coordenadas saem arredondadas em 3 casas")
  void arredondaCoordenadas() {
    User u = profissional(1L, "u", 0, endereco(11L, -8.04761234, -34.87749));

    ProfessionalMapDTO dto = ProfessionalMapDTO.fromUsers(List.of(u), null, null).get(0);

    assertEquals(-8.048, dto.locations().get(0).latitude());
    assertEquals(-34.877, dto.locations().get(0).longitude());
  }

  @Test
  @DisplayName("A distância do profissional é a do endereço mais próximo")
  void distanciaDoEnderecoMaisProximo() {
    User u = profissional(1L, "u", 0, endereco(11L, 0.0, 0.5), endereco(12L, 0.0, 0.01));

    ProfessionalMapDTO dto = ProfessionalMapDTO.fromUsers(List.of(u), 0.0, 0.0).get(0);

    assertEquals(1.11, dto.distanceKm(), 0.01);
    assertEquals(2, dto.locations().size());
  }
}
