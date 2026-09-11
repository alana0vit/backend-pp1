package br.com.conectaPro.Demand;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserRepository;
import br.com.conectaPro.model.demand.DemandRepository;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.demand.DemandStatus;
import jakarta.transaction.Transactional;

//utilizando os testes h2
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DemandServiceIT {

    @Autowired
    private DemandService demandService;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        demandRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar uma demanda com sucesso")
    void deveCriarDemandaComSucesso() {
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descrição teste");

        Demand resultado = demandService.save(demanda);

        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("Demanda teste", resultado.getTitle());
    }

    @Test
    @DisplayName("Deve trazer todas as demandas")
    void deveBuscarTodasDemandas() {

        Demand d1 = new Demand();
        d1.setTitle("Demanda 1");
        d1.setDescription("Descrição 1");
        demandService.save(d1);

        Demand d2 = new Demand();
        d2.setTitle("Demanda 2");
        d2.setDescription("Descrição 2");
        demandService.save(d2);

        List<Demand> demandas = demandService.getAll();

        assertNotNull(demandas);
        assertEquals(2, demandas.size());
    }

    @Test
    @DisplayName("Deve buscar demanda por ID com sucesso")
    void deveBuscarDemandaPorId() {
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descricao teste");
        Demand salva = demandService.save(demanda);

        Demand buscada = demandService.getById(salva.getId());

        assertNotNull(buscada);
        assertEquals("Demanda teste", buscada.getTitle());
    }


    @Test
    @DisplayName("Deve levantar uma exceção de demanda ID não encontrada")
    void excecaoDemandaId() {
        Long idErrado = 2L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            demandService.getById(idErrado);
        });

        assertEquals("Demanda não encontrada com ID: " + idErrado, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar status de demanda fechada")
    void ExcesaoDemandaStatus() {
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descricao teste");
        demanda.setDemandStatus(DemandStatus.FECHADO);
        Demand salva = demandRepository.save(demanda);

        Demand demandaAtualizada = new Demand();
        demandaAtualizada.setTitle("Demanda teste atualizada");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            demandService.update(salva.getId(), demandaAtualizada);
        });

        assertEquals("Não é permitido editar um serviço aceito ou finalizado!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve reatribuir demanda com sucesso")
    void deveReatribuirProfissionalComSucesso() {
        User profissional = new User();
        profissional.setName("João Profissional");
        User profissionalSalvo = userRepository.save(profissional);

        Demand demanda = new Demand();
        demanda.setTitle("Demanda Rejeitada");
        demanda.setDescription("Descrição da demanda");
        demanda.setDemandStatus(DemandStatus.REJEITADO);
        Demand salva = demandRepository.save(demanda);

        Demand reatribuida = demandService.reassign(salva.getId(), profissionalSalvo.getId());

        assertEquals(DemandStatus.ABERTO, reatribuida.getDemandStatus());
        assertEquals(profissionalSalvo.getId(), reatribuida.getProfessionalId().getId());
    }

    @Test
    @DisplayName("Deve levantar uma excecao ao tentar reatribuir demanda")
    void excecaoReatribuirDemanda(){
        User profissional = new User();
        profissional.setName("João Profissional");
        User profissionalSalvo = userRepository.save(profissional);

        Demand demanda = new Demand();
        demanda.setTitle("Demanda Rejeitada");
        demanda.setDescription("Descrição da demanda");
        demanda.setDemandStatus(DemandStatus.ABERTO);
        Demand salva = demandRepository.save(demanda);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            demandService.reassign(salva.getId(), profissionalSalvo.getId());
        });

        assertEquals("Apenas demandas rejeitadas ou expiradas podem ser reatribuídas.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve fazer update de demanda com sucesso")
    void AtualizaDemandaComSucesso(){
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descrição da demanda");
        demanda.setDemandStatus(DemandStatus.ABERTO);
        Demand salva = demandRepository.save(demanda);

        Demand atualizada = demandService.updateStatus(salva.getId(), DemandStatus.AGUARDANDO);

        assertEquals(DemandStatus.AGUARDANDO, atualizada.getDemandStatus());
    }

    @Test
    @DisplayName("Deve levantar excecao ao tentar atualizar status de demanda fechada")
    void ExcecaoAtualizaDemandaFechada(){
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descrição da demanda");
        demanda.setDemandStatus(DemandStatus.FECHADO);
        Demand salva = demandRepository.save(demanda);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            demandService.updateStatus(salva.getId(), DemandStatus.AGUARDANDO);
        });

        assertEquals("Não é possível alterar o status de uma demanda fechada.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve levantar excecao ao tentar atualizar uma demanda reijetada")
    void ExcecaoAtualizarDemandaRejeitada(){
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descrição da demanda");
        demanda.setDemandStatus(DemandStatus.REJEITADO);
        Demand salva = demandRepository.save(demanda);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            demandService.updateStatus(salva.getId(), DemandStatus.ABERTO);
        });

        assertEquals("Não é permitido reabrir uma demanda por este endpoint. Use /reassign.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover uma imagem com sucesso")
    void RemovendoImagemSucesso(){
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descrição da demanda");
        demanda.setImgUrl(new ArrayList<>(List.of("imagem1", "imagem2")));
        Demand salva = demandRepository.save(demanda);

        Demand resultado = demandService.removeImage(salva.getId(), "imagem1");

        assertEquals(1, resultado.getImgUrl().size());
        assertEquals("imagem2", resultado.getImgUrl().get(0));
    }

    @Test
    @DisplayName("Deve levantar excecao ao tentar remover uma imagem que não existe nessa demanda")
    void excecaoRemoverImagem(){
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descrição da demanda");
        Demand salva = demandRepository.save(demanda);

        String nomeArquivoInexistente = "imagemInexistente.jpg";

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            demandService.removeImage(salva.getId(), nomeArquivoInexistente);
        });

        assertEquals("Imagem não encontrada nesta demanda: " + nomeArquivoInexistente, exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover uma demanda")
    void removerDemanda(){
        Demand demanda = new Demand();
        demanda.setTitle("Demanda teste");
        demanda.setDescription("Descrição da demanda");
        demanda.setEnabled(Boolean.TRUE);
        Demand salva = demandRepository.save(demanda);

        demandService.delete(salva.getId());

        assertFalse(demandRepository.existsById(salva.getId()));
    }
}