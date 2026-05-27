package br.com.conectaPro.model.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.transaction.Transactional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository repository;

    @Transactional
    public Rating save(Rating rating) {
        rating.setEnabled(Boolean.TRUE);
        return repository.save(rating);
    }

    public List<Rating> getAll() {
        return repository.findAll();
    }

    public Rating getById(Long id) {

        return repository.findById(id).get();
    }

    @Transactional
    public void update(Long id, Rating ratingChanged) {

        Rating rating = repository.findById(id).get();
        rating.setService(ratingChanged.getService());
        rating.setEvaluatingPerson(ratingChanged.getEvaluatingPerson());
        rating.setPersonEvaluated(ratingChanged.getPersonEvaluated());
        rating.setPoints(ratingChanged.getPoints());
        rating.setDescription(ratingChanged.getDescription());
        rating.setEvaluateDate(ratingChanged.getEvaluateDate());
        rating.setAnonymous(ratingChanged.getAnonymous());
        rating.setStatus(ratingChanged.getStatus());
        repository.save(rating);
    }

    @Transactional
    public void delete(Long id) {

        Rating rating = repository.findById(id).get();
        rating.setEnabled(Boolean.FALSE);

        repository.save(rating);
    }
}
