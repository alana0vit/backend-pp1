package br.com.conectaPro.Privacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.conectaPro.dto.PublicUserDTO;
import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PublicUserDTOTest {

  private List<String> campos(Class<?> tipo) {
    return Arrays.stream(tipo.getRecordComponents()).map(RecordComponent::getName).toList();
  }

  private User profissionalCompleto() {
    AddressUser endereco = new AddressUser();
    endereco.setId(7L);
    endereco.setStreet("Rua Secreta");
    endereco.setNumber("123");
    endereco.setNeighborhood("Boa Viagem");
    endereco.setCity("Recife");
    endereco.setState("PE");
    endereco.setZipCode("51021-000");
    endereco.setLatitude(-8.04761234);
    endereco.setLongitude(-34.87749);

    Category categoria = new Category();
    categoria.setId(3L);
    categoria.setName("Eletricista");

    User u = new User();
    u.setId(1L);
    u.setName("Ana");
    u.setEmail("ana@teste.com");
    u.setPhone("81999998888");
    u.setRegistryId("52998224725");
    u.setBirthDate(LocalDate.of(1990, 5, 15));
    u.setPhoto("foto.png");
    u.setRating(4.5);
    u.setUserType(UserType.PROFESSIONAL);
    u.setVerified(true);
    u.setActivePlanName("Ouro");
    u.setAdresses(List.of(endereco));
    u.setCategories(List.of(categoria));
    return u;
  }

  @Test
  @DisplayName("O perfil público não expõe dados pessoais nem endereço completo")
  void naoExpoeDadosSensiveis() {
    List<String> proibidos =
        List.of(
            "email", "phone", "registryId", "birthDate", "password", "street", "number", "zipCode",
            "supplement");

    List<String> todos = new java.util.ArrayList<>(campos(PublicUserDTO.class));
    todos.addAll(campos(PublicUserDTO.PublicAddressDTO.class));

    for (String proibido : proibidos) {
      assertFalse(todos.contains(proibido), "campo sensível exposto: " + proibido);
    }
  }

  @Test
  @DisplayName("Mantém as chaves que o front já lê (inclusive 'adresses')")
  void mantemChavesDoFront() {
    assertEquals(
        List.of(
            "id", "name", "photo", "rating", "userType", "verified", "activePlanName",
            "categories", "adresses"),
        campos(PublicUserDTO.class));

    List<String> endereco = campos(PublicUserDTO.PublicAddressDTO.class);
    assertTrue(endereco.contains("neighborhood"));
    assertTrue(endereco.contains("city"));
  }

  @Test
  @DisplayName("Copia os valores públicos e arredonda as coordenadas")
  void mapeiaEArredonda() {
    PublicUserDTO dto = PublicUserDTO.fromEntity(profissionalCompleto());

    assertEquals("Ana", dto.name());
    assertEquals(UserType.PROFESSIONAL, dto.userType());
    assertEquals("Ouro", dto.activePlanName());
    assertEquals("Eletricista", dto.categories().get(0).name());
    assertEquals("Boa Viagem", dto.adresses().get(0).neighborhood());
    assertEquals(-8.048, dto.adresses().get(0).latitude());
    assertEquals(-34.877, dto.adresses().get(0).longitude());
  }

  @Test
  @DisplayName("Listas nulas viram listas vazias")
  void listasNulas() {
    User u = new User();
    u.setId(1L);
    u.setName("Sem listas");

    PublicUserDTO dto = PublicUserDTO.fromEntity(u);

    assertTrue(dto.categories().isEmpty());
    assertTrue(dto.adresses().isEmpty());
  }
}
