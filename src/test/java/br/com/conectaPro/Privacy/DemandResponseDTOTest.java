package br.com.conectaPro.Privacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.conectaPro.dto.DemandResponseDTO;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandStatus;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DemandResponseDTOTest {

  private User usuario(Long id, String nome, UserType tipo) {
    User u = new User();
    u.setId(id);
    u.setName(nome);
    u.setEmail(nome + "@teste.com");
    u.setPhone("81999998888");
    u.setRegistryId("52998224725");
    u.setBirthDate(LocalDate.of(1990, 5, 15));
    u.setUserType(tipo);
    return u;
  }

  private Demand demanda(DemandStatus status) {
    Demand d = new Demand();
    d.setId(10L);
    d.setTitle("Troca de tomada");
    d.setClientId(usuario(1L, "cliente", UserType.CLIENT));
    d.setProfessionalId(usuario(2L, "profissional", UserType.PROFESSIONAL));
    d.setDemandStatus(status);
    return d;
  }

  private List<String> campos(Class<?> tipo) {
    return Arrays.stream(tipo.getRecordComponents()).map(RecordComponent::getName).toList();
  }

  @Test
  @DisplayName("Contrato com o front: as chaves da demanda continuam as mesmas")
  void mantemChavesDaDemanda() {
    assertEquals(
        List.of(
            "id", "code", "title", "description", "imgUrl", "suggestedValue", "finalValue",
            "suggestedDate", "openedAt", "addressId", "categoryId", "clientId", "professionalId",
            "demandStatus"),
        campos(DemandResponseDTO.class));
  }

  @Test
  @DisplayName("O usuário aninhado não traz CPF/CNPJ, nascimento nem endereços")
  void usuarioAninhadoSemDadosSensiveis() {
    List<String> party = campos(DemandResponseDTO.PartyDTO.class);

    for (String proibido : List.of("registryId", "birthDate", "adresses", "password")) {
      assertFalse(party.contains(proibido), "campo sensível exposto: " + proibido);
    }
    assertTrue(party.containsAll(List.of("id", "name", "phone", "email")));
  }

  @Test
  @DisplayName("Antes do pagamento (qualquer status que não seja AGUARDANDO/FECHADO) não há contato")
  void contatoOcultoAntesDoPagamento() {
    for (DemandStatus status :
        List.of(
            DemandStatus.ABERTO,
            DemandStatus.AGUARDANDO_PAGAMENTO,
            DemandStatus.REJEITADO,
            DemandStatus.EXPIRADO)) {
      DemandResponseDTO dto = DemandResponseDTO.fromEntity(demanda(status));

      assertNull(dto.professionalId().phone(), "telefone do profissional vazou em " + status);
      assertNull(dto.professionalId().email(), "e-mail do profissional vazou em " + status);
      assertNull(dto.clientId().phone(), "telefone do cliente vazou em " + status);
      assertNull(dto.clientId().email(), "e-mail do cliente vazou em " + status);
      assertNotNull(dto.professionalId().name());
    }
  }

  @Test
  @DisplayName("Com o pagamento aprovado (AGUARDANDO/FECHADO) o contato é liberado")
  void contatoLiberadoAposPagamento() {
    for (DemandStatus status : List.of(DemandStatus.AGUARDANDO, DemandStatus.FECHADO)) {
      DemandResponseDTO dto = DemandResponseDTO.fromEntity(demanda(status));

      assertEquals("81999998888", dto.professionalId().phone());
      assertEquals("profissional@teste.com", dto.professionalId().email());
      assertEquals("81999998888", dto.clientId().phone());
      assertEquals("cliente@teste.com", dto.clientId().email());
    }
  }

  @Test
  @DisplayName("A regra de liberação do contato está documentada em CONTACT_RELEASED")
  void regraDeLiberacao() {
    assertEquals(
        EnumSet.of(DemandStatus.AGUARDANDO, DemandStatus.FECHADO),
        DemandResponseDTO.CONTACT_RELEASED);
  }

  @Test
  @DisplayName("Usuário nulo continua nulo e imgUrl nulo vira lista vazia")
  void nulosSaoTratados() {
    Demand d = new Demand();
    d.setId(1L);
    d.setDemandStatus(DemandStatus.ABERTO);

    DemandResponseDTO dto = DemandResponseDTO.fromEntity(d);

    assertNull(dto.clientId());
    assertNull(dto.professionalId());
    assertTrue(dto.imgUrl().isEmpty());
  }
}
