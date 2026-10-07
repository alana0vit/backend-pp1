package br.com.conectaPro.Privacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandRepository;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserService;
import br.com.conectaPro.security.EmailService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DemandAccessTest {

  @Mock private DemandRepository demandRepository;
  @Mock private UserService userService;
  @Mock private EmailService emailService;
  @InjectMocks private DemandService demandService;

  private User usuario(Long id) {
    User u = new User();
    u.setId(id);
    return u;
  }

  private Demand demanda(Long id, Long clienteId, Long profissionalId) {
    Demand d = new Demand();
    d.setId(id);
    d.setClientId(usuario(clienteId));
    d.setProfessionalId(usuario(profissionalId));
    return d;
  }

  @Test
  @DisplayName("Lista só as demandas em que o usuário é cliente ou profissional")
  void listaSoAsDoUsuario() {
    Demand minhaComoCliente = demanda(1L, 10L, 20L);
    Demand minhaComoProfissional = demanda(2L, 30L, 10L);
    Demand deOutros = demanda(3L, 40L, 50L);
    when(demandRepository.findAll())
        .thenReturn(List.of(minhaComoCliente, minhaComoProfissional, deOutros));

    List<Demand> resultado = demandService.getAllForUser(10L);

    assertEquals(List.of(1L, 2L), resultado.stream().map(Demand::getId).toList());
  }

  @Test
  @DisplayName("Participante consegue abrir a demanda pelo id")
  void participanteAbre() {
    when(demandRepository.findById(1L)).thenReturn(Optional.of(demanda(1L, 10L, 20L)));

    assertEquals(1L, demandService.getByIdForUser(1L, 20L).getId());
  }

  @Test
  @DisplayName("Quem não participa recebe 'não encontrada' (404), sem revelar que existe")
  void naoParticipanteRecebe404() {
    when(demandRepository.findById(1L)).thenReturn(Optional.of(demanda(1L, 10L, 20L)));

    assertThrows(NoSuchElementException.class, () -> demandService.getByIdForUser(1L, 99L));
    assertThrows(NoSuchElementException.class, () -> demandService.getByIdForUser(1L, null));
  }

  @Test
  @DisplayName("Demanda sem profissional ou sem cliente não quebra a checagem")
  void participantesNulos() {
    Demand semProfissional = new Demand();
    semProfissional.setId(5L);
    semProfissional.setClientId(usuario(10L));
    when(demandRepository.findAll()).thenReturn(List.of(semProfissional));

    assertEquals(1, demandService.getAllForUser(10L).size());
    assertEquals(0, demandService.getAllForUser(99L).size());
  }
}
