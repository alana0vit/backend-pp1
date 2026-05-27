package br.com.conectaPro.api.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.model.rating.RatingService;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.rating.Rating;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@RestController
@RequestMapping("/api/rating")
@CrossOrigin
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private DemandService demandService;

    @PostMapping()
    public ResponseEntity<Rating> save(@RequestBody RatingRequest request) {
        Rating ratingNew = request.build();
        ratingNew.setService(demandService.getById(request.getService()));
        Rating rating = ratingService.save(ratingNew);
        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }
    
    @GetMapping
    public List<Rating> getAll() {
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public Rating getById(@PathVariable Long id) {
        return ratingService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rating> update(@PathVariable("id") Long id, @RequestBody RatingRequest request) {

        Rating rating = request.build();
        rating.setService(demandService.getById(request.getService()));
        ratingService.update(id, rating);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        ratingService.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
