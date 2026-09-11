package br.com.conectaPro.Rating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.conectaPro.model.rating.RatingRepository;
import br.com.conectaPro.model.user.UserRepository;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.rating.RatingService;
import br.com.conectaPro.model.rating.Rating;
import br.com.conectaPro.model.demand.DemandRepository;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.rating.EvaluateStatus;
import br.com.conectaPro.dto.FinishRatingDTO;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class RatingServiceIT {
    
    @Autowired 
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private RatingService ratingService;

    @Autowired 
    private DemandRepository demandRepository;

    private User avaliador;
    private User profissional;
    private Demand demanda;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        userRepository.deleteAll();
        demandRepository.deleteAll();

        avaliador = new User();
        avaliador.setName("Avaliador");
        avaliador = userRepository.save(avaliador);

        profissional = new User();
        profissional.setName("Profissional Avaliado");
        profissional = userRepository.save(profissional);

        demanda = new Demand();
        demanda.setTitle("Demanda de Teste");
        demanda.setDescription("Descricao de Teste");
        demanda = demandRepository.save(demanda);
    }

    @Test 
    @DisplayName("Deve salvar uma avaliacao")
    void salvarAvaliacao(){

        Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        
        Rating ratingSalvo = ratingService.save(avaliacao);

        assertNotNull(ratingSalvo.getId());
        assertEquals(EvaluateStatus.PENDENTE, ratingSalvo.getStatus());
    }

    @Test
    @DisplayName("Deve finalizar uma avaliacao")
    void finalizarAvaliacao(){

        Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        Rating ratingSalvo = ratingService.save(avaliacao);

        FinishRatingDTO finalizar = new FinishRatingDTO();
        finalizar.setApproved(true);
        finalizar.setPoints(4);
        finalizar.setDescription("finalizar teste");
        finalizar.setAnonymous(false);

        ratingService.finish(ratingSalvo.getId(), finalizar);

        Rating ratingFinalizado = ratingRepository.findById(ratingSalvo.getId()).orElseThrow();

        assertEquals(EvaluateStatus.COMPLETO, ratingFinalizado.getStatus());
        assertEquals("finalizar teste", ratingFinalizado.getDescription());
    }

    @Test 
    @DisplayName("Deve levantar excecao ao tentar finalizar uma avaliacao ja finalizada")
    void finalizarAvaliacaoJaFinalizada(){

        Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        Rating ratingSalvo = ratingService.save(avaliacao);

        FinishRatingDTO finalizar = new FinishRatingDTO();
        finalizar.setApproved(true);
        finalizar.setPoints(4);
        finalizar.setDescription("finalizar teste");
        finalizar.setAnonymous(false);

        ratingService.finish(ratingSalvo.getId(), finalizar);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.finish(ratingSalvo.getId(), finalizar);
        });

        assertEquals("Avaliação já finalizada.", exception.getMessage());
    }

    @Test 
    @DisplayName("Deve levantar excecao ao tentar finalizar uma avaliacao com aprovado=false")
    void DeveReijeitarFinalizar(){
        Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        Rating ratingSalvo = ratingService.save(avaliacao);

        FinishRatingDTO finalizar = new FinishRatingDTO();
        finalizar.setApproved(false);

        ratingService.finish(ratingSalvo.getId(), finalizar);

        Rating atualizado = ratingRepository.findById(ratingSalvo.getId()).orElseThrow();
        assertEquals(EvaluateStatus.REJEITADO, atualizado.getStatus());
    }

    @Test
    @DisplayName("Deve levantar excecao ao tentar finalizar uma avaliacao com approved=null")
    void DeveLevantarExcecaoAprovedNull(){
        Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        Rating ratingSalvo = ratingService.save(avaliacao);

        FinishRatingDTO finalizar = new FinishRatingDTO();
        finalizar.setApproved(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.finish(ratingSalvo.getId(), finalizar);
        });
        assertEquals("approved é obrigatório.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve levantar excecao ao tentar finalizar uma avaliacao com pontos invalidos")
    void DeveLevantarExcecaoPontosInvalidos(){
        Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        Rating ratingSalvo = ratingService.save(avaliacao);

        FinishRatingDTO finalizar = new FinishRatingDTO();
        finalizar.setApproved(true);
        finalizar.setPoints(7);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.finish(ratingSalvo.getId(), finalizar);
        });
        assertEquals("Pontuação inválida.", exception.getMessage());
    }

    @Test 
    @DisplayName("Deve levantar excecao ao tentar finalziar uma avalicao com pontuacao=null")
    void DeveLevantarExcecaoPontosNull(){
        Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        Rating ratingSalvo = ratingService.save(avaliacao);

        FinishRatingDTO finalizar = new FinishRatingDTO();
        finalizar.setApproved(true);
        finalizar.setPoints(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.finish(ratingSalvo.getId(), finalizar);
        });
        assertEquals("Pontuação obrigatória.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar uma avaliacao do usuario")
    void BuscarAvaliacao(){
         Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        Rating ratingSalvo = ratingService.save(avaliacao);

        Rating resultado = ratingService.getUserRating(profissional.getId(), ratingSalvo.getId());
        assertEquals(ratingSalvo.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Deve levantar excecao de avaliacao nao pertence a usuario")
    void ExcecaoNaoPertence(){
        Rating avaliacao = new Rating();
        avaliacao.setEvaluatingPerson(avaliador);
        avaliacao.setPersonEvaluated(profissional);
        avaliacao.setService(demanda);
        avaliacao.setStatus(EvaluateStatus.PENDENTE);
        Rating ratingSalvo = ratingService.save(avaliacao);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ratingService.getUserRating(avaliador.getId(), ratingSalvo.getId());
        });

        assertEquals("Essa avaliação não pertence ao usuário informado", exception.getMessage());
    }

}
