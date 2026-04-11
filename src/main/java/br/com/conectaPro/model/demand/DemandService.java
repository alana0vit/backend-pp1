package br.com.conectaPro.model.demand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.conectaPro.model.demand.DemandRepository;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class DemandService {
    @Autowired
    private DemandRepository repository;

    @Transactional
    public Demand save(Demand demand) {

        demand.setEnabled(Boolean.TRUE);
        return repository.save(demand);
    }

    public List<Demand> getAll() {

        return repository.findAll();
    }

    public Demand getById(Long id) {

        return repository.findById(id).get();
    }

    @Transactional
    public void update(Long id, Demand demandChanged) {

        Demand demand = repository.findById(id).get();
        demand.setCode(demandChanged.getCode());
        demand.setTitle(demandChanged.getTitle());
        demand.setDescription(demandChanged.getDescription());
        demand.setImgUrl(demandChanged.getImgUrl());
        demand.setAddressId(demandChanged.getAddressId());
        demand.setCategoryId(demandChanged.getCategoryId());
        demand.setClientId(demandChanged.getClientId());
        demand.setProfessionalId(demandChanged.getProfessionalId());

        repository.save(demand);
    }

    @Transactional
    public void delete(Long id) {

        Demand demand = repository.findById(id).get();
        demand.setEnabled(Boolean.FALSE);

        repository.save(demand);
    }
}
