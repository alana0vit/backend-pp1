package br.com.conectaPro.model.rating;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class RatingService {

    private RatingRepository ratingRepository;

    @Transactional
    public Rating save(Rating rating) {
        rating.setEnabled(Boolean.TRUE);
        return ratingRepository.save(rating);
    }
    
}
