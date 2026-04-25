package br.com.conectaPro.api.demand;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.dto.ReassignRequestDTO;
import br.com.conectaPro.dto.StatusUpdateDTO;
import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.category.CategoryService;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.demand.DemandStatus;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
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
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid DemandRequest request) {

        // TODO: Futuramente, mover essas validações para um @RestControllerAdvice
        // usando exceptions customizadas (ex: EntityNotFoundException).

        try {
            // As buscas com validação. Se não existir, caem no catch e retornamos 404.
            User client = userService.getById(request.getClientId());
            User professional = userService.getById(request.getProfessionalId());
            Category category = categoryService.getById(request.getCategoryId());
            AddressUser address = userService.getAddressById(request.getAddressId());

            Demand demandNew = request.build();
            demandNew.setCategoryId(category);
            demandNew.setAddressId(address);
            demandNew.setClientId(client);
            demandNew.setProfessionalId(professional);
            demandNew.setDemandStatus(DemandStatus.OPENED);
            String demandCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            demandNew.setCode(demandCode);

            Demand demand = demandService.save(demandNew);
            return new ResponseEntity<>(demand, HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            // Captura o erro do .get() dos services e devolve 400 Bad Request ou 404 Not
            // Found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Um dos IDs informados (Cliente, Profissional, Categoria ou Endereço) não existe.");
        }
    }

    @GetMapping("/user")
    public List<Demand> getAll() {
        return demandService.getAll();
    }

    @GetMapping("/user/{id}")
    public Demand getById(@PathVariable @NonNull Long id) {
        return demandService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Demand> update(@PathVariable("id") @NonNull Long id, @RequestBody DemandRequest request) {

        Demand demand = request.build();
        demand.setCategoryId(categoryService.getById(request.getCategoryId()));
        demandService.update(id, demand);
        return ResponseEntity.ok().build();

    }

    @PatchMapping("/{id}/reassign")
    public ResponseEntity<Demand> reassignProfessional(
            @PathVariable @NonNull Long id,
            @RequestBody ReassignRequestDTO request) {
        Demand updatedDemand = demandService.reassign(id, request.professionalId());
        return ResponseEntity.ok(updatedDemand);

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Demand> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDTO request) {
        Demand updated = demandService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {

        demandService.delete(id);
        return ResponseEntity.ok().build();
    }

}
