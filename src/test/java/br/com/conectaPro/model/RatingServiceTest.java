package br.com.conectaPro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.conectaPro.dto.FinishRatingDTO;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.rating.EvaluateStatus;
import br.com.conectaPro.model.rating.Rating;
import br.com.conectaPro.model.rating.RatingRepository;
import br.com.conectaPro.model.rating.RatingService;
import br.com.conectaPro.model.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    
    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingService ratingService; 

    @Test
    void RetornandoAvaliacaoUsuarioId(){
        Long usuarioId = 1L;

        Rating avaliacao = new Rating();
        avaliacao.setId(2L);

        when(ratingRepository.findRatingsByUser(usuarioId)).thenReturn(List.of(avaliacao));

        List<Rating> resultado = ratingService.getByUser(usuarioId);

        assertNotNull(resultado);
        assertEquals(2L, avaliacao.getId());
        assertEquals(1, resultado.size());
    }

    @Test
    void TodasAvaliacoesUsuarioId(){
         Long usuarioId = 1L;

        Rating avaliacaoUm = new Rating();
        avaliacaoUm.setId(2L);

        Rating avaliacaoDois = new Rating();
        avaliacaoDois.setId(3L);

        List<Rating> listaEsperado = List.of(avaliacaoUm,avaliacaoDois);

        when(ratingRepository.findRatingsByEvaluator(1L)).thenReturn(listaEsperado);

        List<Rating> resultado = ratingService.getByEvaluator(usuarioId);

        assertNotNull(resultado);
        assertEquals(2L, avaliacaoUm.getId());
        assertEquals(3L, avaliacaoDois.getId());
    }

    @Test
    void SalvandoRating(){
        Demand demanda = new Demand();
        demanda.setId(1L);

        User usuario = new User();
        usuario.setId(2L);

        Rating avaliando = new Rating();
        avaliando.setService(demanda);
        avaliando.setEvaluatingPerson(usuario);

        when(ratingRepository.findByServiceIdAndEvaluatingPersonId(1L, 2L))
                .thenReturn(Collections.emptyList());

        when(ratingRepository.save(any(Rating.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Rating resultado = ratingService.save(avaliando);

        assertNotNull(resultado);
        assertTrue(resultado.getEnabled());
    }

    @Test
    void AvaliacaoUsuarioId(){

        User pessoaAvaliada = new User();
        pessoaAvaliada.setId(1L);

        Rating avaliacao = new Rating();
        avaliacao.setId(2L);
        avaliacao.setPersonEvaluated(pessoaAvaliada);

        when(ratingRepository.findById(2L)).thenReturn(Optional.of(avaliacao));

        Rating resultado = ratingService.getUserRating(1L, 2L);

        assertNotNull(resultado);
        assertEquals(1L, pessoaAvaliada.getId());
        assertEquals(2L, avaliacao.getId());
    }

    @Test
    void LevantandoExcecaoUsuarioId(){
        User pessoaAvaliada = new User();
        pessoaAvaliada.setId(1L);

        Rating avaliacao = new Rating();
        avaliacao.setId(2L);
        avaliacao.setPersonEvaluated(pessoaAvaliada);

        when(ratingRepository.findById(2L)).thenReturn(Optional.of(avaliacao));

        IllegalStateException resultado = assertThrows(IllegalStateException.class, () -> { ratingService.getUserRating(3L, 2L);});

        assertEquals("Essa avaliação não pertence ao usuário informado", resultado.getMessage());
    }

    @Test
    void ErroSalvandoRating(){
        Demand demanda = new Demand();
        demanda.setId(1L);

        User usuario = new User();
        usuario.setId(2L);

        Rating avaliacaoExistente = new Rating();
        avaliacaoExistente.setStatus(EvaluateStatus.PENDENTE);

        Rating avaliando = new Rating();
        avaliando.setService(demanda);
        avaliando.setEvaluatingPerson(usuario);

        when(ratingRepository.findByServiceIdAndEvaluatingPersonId(1L,2L)).
            thenReturn(List.of(avaliacaoExistente));

        IllegalStateException resultado = assertThrows(IllegalStateException.class, () ->{ratingService.save(avaliando);});

        assertEquals("Você já enviou uma avaliação para esta demanda.", resultado.getMessage());
    }
    
    @Test
    void FinalizandoRatingComSucesso(){
        Long id = 1L;

        User usuario = new User();
        usuario.setId(2L);

        Rating avaliando = new Rating();
        avaliando.setId(id);
        avaliando.setStatus(EvaluateStatus.PENDENTE);
        avaliando.setPersonEvaluated(usuario);

        FinishRatingDTO request = new FinishRatingDTO();
        request.setApproved(true);
        request.setPoints(5);
        request.setDescription("foda pra crll");
        request.setAnonymous(false);

        when(ratingRepository.findById(id)).thenReturn(Optional.of(avaliando));
        when(ratingRepository.calculateAverageByUser(2L)).thenReturn(4.8);

        ratingService.finish(id, request);

        assertEquals(EvaluateStatus.COMPLETO, avaliando.getStatus());
        assertEquals(5, avaliando.getPoints());
        assertEquals("foda pra crll", avaliando.getDescription());
        assertEquals(4.8, usuario.getRating());
    }

    @Test
    void recusandoAvaliacao(){
        Long id = 1L;

        Rating avaliando = new Rating();
        avaliando.setId(id);
        avaliando.setStatus(EvaluateStatus.PENDENTE);

        FinishRatingDTO request = new FinishRatingDTO();
        request.setApproved(false);

        when(ratingRepository.findById(id)).thenReturn(Optional.of(avaliando));

        ratingService.finish(id, request);

        assertEquals(EvaluateStatus.REJEITADO, avaliando.getStatus());
    }

    @Test
    void LevantandoExcecao(){
        Long id = 1L;

        Rating avaliando = new Rating();
        avaliando.setId(id);
        avaliando.setStatus(EvaluateStatus.PENDENTE);

        FinishRatingDTO request = new FinishRatingDTO();
        request.setApproved(true);
        request.setPoints(6);

        when(ratingRepository.findById(id)).thenReturn(Optional.of(avaliando));

        RuntimeException resultado = assertThrows(RuntimeException.class, ()-> {ratingService.finish(id, request);});

        assertEquals("Pontuação inválida.", resultado.getMessage());
    }
    
}
