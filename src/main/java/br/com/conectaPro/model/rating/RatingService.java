package br.com.conectaPro.model.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import br.com.conectaPro.dto.FinishRatingDTO;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Rating save(Rating rating) {
        rating.setEnabled(Boolean.TRUE);
        return repository.save(rating);
    }

    public List<Rating> getByUser(Long userId) {
        return repository.findRatingsByUser(userId);
    }

    public Rating getUserRating(Long userId, Long ratingId) {

        Rating rating = repository.findById(ratingId)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada"));

        if (!rating.getPersonEvaluated().getId().equals(userId)) {
            throw new IllegalStateException("Essa avaliação não pertence ao usuário informado");
        }

        return rating;
    }

    @Transactional
    public void finish(Long id, FinishRatingDTO request) {

        Rating rating = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada."));

        if (rating.getStatus() != EvaluateStatus.PENDENTE) {
            throw new RuntimeException("Avaliação já finalizada.");
        }

        if (request.getApproved() == null) {
            throw new RuntimeException("approved é obrigatório.");
        }

        // rejeição
        if (Boolean.FALSE.equals(request.getApproved())) {

            rating.setStatus(EvaluateStatus.REJEITADO);

            repository.save(rating);
            return;
        }

        // validações
        if (request.getPoints() == null) {
            throw new RuntimeException("Pontuação obrigatória.");
        }

        if (request.getPoints() < 1 || request.getPoints() > 5) {
            throw new RuntimeException("Pontuação inválida.");
        }

        // conclusão
        rating.setPoints(request.getPoints());
        rating.setDescription(request.getDescription());
        rating.setAnonymous(request.getAnonymous());
        rating.setStatus(EvaluateStatus.COMPLETO);

        repository.save(rating);

        // recalcula média
        User evaluated = rating.getPersonEvaluated();

        Double avg = repository.calculateAverageByUser(
                evaluated.getId());

        evaluated.setRating(avg != null ? avg : 0.0);

        userRepository.save(evaluated);
    }
}
