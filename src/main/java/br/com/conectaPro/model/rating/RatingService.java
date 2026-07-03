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
        // Impede avaliação duplicada para a mesma demanda pelo mesmo avaliador
        List<Rating> existentes = repository.findByServiceIdAndEvaluatingPersonId(
                rating.getService().getId(),
                rating.getEvaluatingPerson().getId());

        boolean jaAvaliou = existentes.stream()
                .anyMatch(r -> r.getStatus() == EvaluateStatus.PENDENTE
                            || r.getStatus() == EvaluateStatus.COMPLETO);

        if (jaAvaliou) {
            throw new IllegalStateException("Você já enviou uma avaliação para esta demanda.");
        }

        rating.setEnabled(Boolean.TRUE);
        return repository.save(rating);
    }

    public List<Rating> getByUser(Long userId) {
        return repository.findRatingsByUser(userId);
    }

    public List<Rating> getByEvaluator(Long userId) {
        return repository.findRatingsByEvaluator(userId);
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
        if (Boolean.FALSE.equals(request.getApproved())) {
            rating.setStatus(EvaluateStatus.REJEITADO);
            repository.save(rating);
            return;
        }
        if (request.getPoints() == null) {
            throw new RuntimeException("Pontuação obrigatória.");
        }
        if (request.getPoints() < 1 || request.getPoints() > 5) {
            throw new RuntimeException("Pontuação inválida.");
        }
        rating.setPoints(request.getPoints());
        rating.setDescription(request.getDescription());
        rating.setAnonymous(request.getAnonymous());
        rating.setStatus(EvaluateStatus.COMPLETO);
        repository.save(rating);

        User evaluated = rating.getPersonEvaluated();
        Double avg = repository.calculateAverageByUser(evaluated.getId());
        evaluated.setRating(avg != null ? avg : 0.0);
        userRepository.save(evaluated);
    }
}