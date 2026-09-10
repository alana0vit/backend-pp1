package br.com.conectaPro.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandRepository;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.demand.DemandStatus;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserService;
import br.com.conectaPro.security.EmailService;
import br.com.conectaPro.util.Util;

@ExtendWith(MockitoExtension.class)
 class DemandServiceTest {
    
    @Mock
    private DemandRepository demandRepository;
    @Mock 
    private UserService userService;
    @Mock 
    private EmailService emailService;
    @InjectMocks
    private DemandService demandService;


    @Test
    @DisplayName("Deve salvar uma demanda com sucesso")
    void SalvaDemanda(){
        Demand demanda = new Demand();
        demanda.setTitle("Concerto da geladeira");
        demanda.setDescription("geladeira com bo");

        when(demandRepository.save(any(Demand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Demand save = demandService.save(demanda);

        assertNotNull(save.getOpenedAt());
        assertEquals("Concerto da geladeira", save.getTitle());
    }

    @Test
    @DisplayName("Deve localizar uma demanda por ID com sucesso")
    void LocalizarDemandaPorId(){
        Demand demanda = new Demand();
        demanda.setId(1L);
        demanda.setTitle("Concerto da geladeira");

        when(demandRepository.findById(1L)).thenReturn(java.util.Optional.of(demanda));

        Demand resultado = demandService.getById(1L);
        assertEquals(demanda, resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao localizar uma demanda por ID que não existe")
    void ErrorAoLocalizarDemandaPorId(){
        Long id = 1L;

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.empty());

       NoSuchElementException exception = assertThrows(
        NoSuchElementException.class,
        () -> demandService.getById(id)
        );

        assertEquals("Demanda não encontrada com ID: " + id, exception.getMessage());
    }

    @Test
    @DisplayName("Deletando demanda por Id")
    void DeletandoDemandaPorId(){
        Long id = 1L;

        Demand demanda = new Demand();
        demanda.setId(id);
        demanda.setEnabled(Boolean.TRUE);

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.of(demanda));

        demandService.delete(id);

        assertEquals(Boolean.FALSE, demanda.getEnabled());
    }

    @Test
    @DisplayName("Nao encontrou a demanda pelo id")
    void ErroDeletarDemandaPorId(){
        Long id = 1L;

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.empty());

        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> demandService.delete(id)
        );

        assertEquals("Demanda não encontrada com ID: " + id, exception.getMessage());
    }

    @Test
    @DisplayName("Update na demanda")
    void UpdateDemanda(){
        Long id = 1L;

        Demand demanda = new Demand();
        demanda.setId(id);
        demanda.setTitle("Concerto da geladeira");
        demanda.setDescription("geladeira com bo");
        demanda.setDemandStatus(DemandStatus.ABERTO);

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.of(demanda));
        when(demandRepository.save(any(Demand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Demand update = new Demand();
        update.setTitle("Concerto da geladeira novo");
        update.setDescription("geladeira ta uma merda");

        demandService.update(id, update);

        assertEquals("Concerto da geladeira novo", demanda.getTitle());
        assertEquals("geladeira ta uma merda", demanda.getDescription());
    }

    @Test
    @DisplayName("Erro ao tentar mudar status da demanda")
    void UpdateDemandaErroStatus(){
        Long id = 1L;

        Demand demanda = new Demand();
        demanda.setId(id);
        demanda.setTitle("Geladeira nova");
        demanda.setDemandStatus(DemandStatus.EXPIRADO);

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.of(demanda));

        Demand update = new Demand();
        update.setTitle("Concerto da geladeira novo");

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> demandService.update(id, update)
        );

        assertEquals("Não é permitido editar um serviço aceito ou finalizado!", exception.getMessage());
    }

    @Test
    @DisplayName("Update no status da demanda")
    void updateDemandaStatus(){
        Long id = 1L; 

        Demand demanda = new Demand();
        demanda.setId(id);
        demanda.setDemandStatus(DemandStatus.ABERTO);

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.of(demanda));
        when(demandRepository.save(any(Demand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        demandService.updateStatus(id, DemandStatus.AGUARDANDO);
        assertEquals(DemandStatus.AGUARDANDO, demanda.getDemandStatus());
    }

    @Test
    @DisplayName("Avaliacao com sucesso")
    void Reassing(){
        Long DemandId = 1L; 
        Long newProfessionalId = 2L;

        Demand demanda = new Demand();
        demanda.setId(DemandId);
        demanda.setDemandStatus(DemandStatus.REJEITADO);

        User profissional = new User();
        profissional.setId(newProfessionalId);

        when(demandRepository.findById(DemandId)).thenReturn(java.util.Optional.of(demanda));
        when(userService.getById(newProfessionalId)).thenReturn(profissional);
        
        when(demandRepository.save(any(Demand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Demand reassing = demandService.reassign(DemandId, newProfessionalId);

        assertEquals(DemandStatus.ABERTO, reassing.getDemandStatus());
        assertEquals(profissional, reassing.getProfessionalId());
        assertNotNull(reassing.getDemandStatus());
    }

    @Test
    @DisplayName("Erro ao tentar encontrar demanda para Avaliacao")
    void ReassingNaoEncontrado(){
        Long DemandId = 1L;
        long ProfissionalId = 2L;

        when(demandRepository.findById(DemandId)).thenReturn(java.util.Optional.empty());

        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> demandService.reassign(DemandId, ProfissionalId)
        );

        assertEquals("Demanda não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Erro ao tentar reatribuir demandas reijadas")
    void ReassingErroStatus(){
        Long DemandId = 1L; 
        Long newProfessionalId = 2L;

        Demand demanda = new Demand();
        demanda.setId(DemandId);
        demanda.setDemandStatus(DemandStatus.ABERTO);

        when(demandRepository.findById(DemandId)).thenReturn(java.util.Optional.of(demanda));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> demandService.reassign(DemandId, newProfessionalId)
        );

        assertEquals("Apenas demandas rejeitadas ou expiradas podem ser reatribuídas.", exception.getMessage());
    }

    @Test
    @DisplayName("Update status com sucesso")
    void updateStatus(){
        Long id = 1L;

        Demand demanda = new Demand();
        demanda.setId(id);
        demanda.setDemandStatus(DemandStatus.ABERTO);

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.of(demanda));
        when(demandRepository.save(any(Demand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        demandService.updateStatus(id, DemandStatus.AGUARDANDO);
        assertEquals(DemandStatus.AGUARDANDO, demanda.getDemandStatus());
    }

    @Test
    @DisplayName("Erro ao tentar mudar status")
    void updateStatusErro(){
        Long id = 1L;

        Demand demanda = new Demand();
        demanda.setId(id);
        demanda.setDemandStatus(DemandStatus.ABERTO);

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.of(demanda));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> demandService.updateStatus(id, DemandStatus.FECHADO)
        );

        assertEquals("Transição inválida: ABERTO → FECHADO", exception.getMessage());
    }

    @Test
    @DisplayName("Erro ao tentar mudar o statu fechado")
    void updateStatusErroDemandaFechada(){
        Long id = 1L;

        Demand demanda = new Demand();
        demanda.setId(id);
        demanda.setDemandStatus(DemandStatus.FECHADO);

        when(demandRepository.findById(id)).thenReturn(java.util.Optional.of(demanda));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> demandService.updateStatus(id, DemandStatus.AGUARDANDO)
        );

        assertEquals("Não é possível alterar o status de uma demanda fechada.", exception.getMessage());
    }

    @Test
    @DisplayName("removendo a imagem da demanda")
    void removeImagem() {

        Long id = 1L;
        String nomeArquivo = "imagem.jpg";

        Demand demanda = new Demand();
        demanda.setId(id);
        demanda.setImgUrl(new ArrayList<>(List.of(
                "imagem.jpg",
                "outra-imagem.jpg"
        )));

        when(demandRepository.findById(id))
                .thenReturn(java.util.Optional.of(demanda));

        when(demandRepository.save(any(Demand.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<Util> utilMock = mockStatic(Util.class)) {

            Demand resultado = demandService.removeImage(id, nomeArquivo);

            assertFalse(resultado.getImgUrl().contains(nomeArquivo));
            assertTrue(resultado.getImgUrl().contains("outra-imagem.jpg"));

            verify(demandRepository).save(demanda);

            utilMock.verify(() -> Util.apagarImagem(nomeArquivo));
        }
    }

}
