package br.com.conectaPro.model.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Rating> getAll() {
        return repository.findAll();
    }

    public Rating getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
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

        evaluated.setRating(avg);

        userRepository.save(evaluated);
    }

    @Transactional
    public void delete(Long id) {

        Rating rating = repository.findById(id).get();
        rating.setEnabled(Boolean.FALSE);

        repository.save(rating);
    }
}
