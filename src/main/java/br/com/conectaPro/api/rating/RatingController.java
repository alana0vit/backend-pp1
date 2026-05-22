package br.com.conectaPro.api.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.model.rating.RatingService;
import br.com.conectaPro.model.rating.Rating;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/rating")
@CrossOrigin
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping()
    public ResponseEntity<Rating> save(@RequestBody RatingRequest request) {
        Rating rating = ratingService.save(request.build());
        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }
    
    
}
