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

import br.com.conectaPro.model.category.CategoryService;
import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/demand")
@CrossOrigin
public class DemandController {
    @Autowired
    private DemandService demandService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Demand> save(@RequestBody @Valid DemandRequest request) {

        Demand demandNew = request.build();
        Category category = categoryService.getById(request.getCategoryId());
        demandNew.setCategoryId(category);
        Demand demand = demandService.save(demandNew);
        return new ResponseEntity<Demand>(demand, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Demand> getAll() {
        return demandService.getAll();
    }


    /** TODO
     * Separação das Demandas por Usuário
     * Atualmente, o DemandController tem um GET /api/demand que traz 
     * todos os pedidos do mundo. 
     * Isso vaza dados de outros clientes.
     * Como resolver? 
     * Criar endpoints específicos para listagem.
     * Um GET /api/demand/client/{id} 
     * OutroGET /api/demand/professional/{id} 
    */ 
    @GetMapping("/{id}")
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
