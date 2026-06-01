package br.com.conectaPro.api.rating;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.dto.FinishRatingDTO;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.rating.EvaluateStatus;
import br.com.conectaPro.model.user.UserService;
import br.com.conectaPro.model.rating.Rating;
import br.com.conectaPro.model.rating.RatingService;
import br.com.conectaPro.model.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/rating")
@CrossOrigin
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private DemandService demandService;

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<?> save(@RequestBody @Valid RatingRequest request) {

        try {
            User evaluator = userService.getById(request.getEvaluatingPerson());
            User evaluated = userService.getById(request.getPersonEvaluated());
            Demand service = demandService.getById(request.getService());

            Rating ratingNew = request.build();
            ratingNew.setService(service);
            ratingNew.setEvaluatingPerson(evaluator);
            ratingNew.setPersonEvaluated(evaluated);
            ratingNew.setStatus(EvaluateStatus.PENDENTE);

            Rating rating = ratingService.save(ratingNew);
            return new ResponseEntity<>(rating, HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Um dos IDs informados (Avaliador, Avaliado ou demanda) não existe.");
        }
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
    public ResponseEntity<Void> finish(@PathVariable Long id, @RequestBody FinishRatingDTO request) {

        ratingService.finish(id, request);

        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        ratingService.delete(id);
        return ResponseEntity.ok().build();
    }

}
