package br.com.conectaPro.api.demand;

import java.util.List;

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

import br.com.conectaPro.model.category.CategoryService;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.user.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/demand")
@CrossOrigin
public class DemandController {
    @Autowired
    private DemandService demandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService addressService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Demand> save(@RequestBody @Valid DemandRequest request) {

        Demand demandNew = request.build();

        demandNew.setCategoryId(categoryService.getById(request.getCategoryId()));
        demandNew.setAddressId(addressService.getAddressById(request.getAddressId()));
        demandNew.setClientId(userService.getById(request.getClientId()));
        demandNew.setProfessionalId(userService.getById(request.getProfessionalId()));

        Demand demand = demandService.save(demandNew);
        return new ResponseEntity<>(demand, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public List<Demand> getAll() {
        return demandService.getAll();
    }

    @GetMapping("/user/{id}")
    public Demand getById(@PathVariable Long id) {
        return demandService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Demand> update(@PathVariable("id") Long id, @RequestBody DemandRequest request) {

        Demand demand = request.build();
        demand.setCategoryId(categoryService.getById(request.getCategoryId()));
        demandService.update(id, demand);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        demandService.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
