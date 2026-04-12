package br.com.conectaPro.api.demand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import java.util.List;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/demand")
@CrossOrigin
public class DemandController {
    @Autowired
    private DemandService demandService;

    @PostMapping
    public ResponseEntity<Demand> save(@RequestBody @Valid DemandRequest request) {

        Demand demand = demandService.save(request.build());
        return new ResponseEntity<Demand>(demand, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Demand> getAll() {
        return demandService.getAll();
    }

    @GetMapping("/{id}")
    public Demand getById(@PathVariable Long id) {
        return demandService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Demand> update(@PathVariable("id") Long id, @RequestBody DemandRequest request) {

        demandService.update(id, request.build());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        demandService.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
