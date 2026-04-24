package br.com.conectaPro.model.demand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserService;
import jakarta.transaction.Transactional;

@Service
public class DemandService {
  @Autowired
  private DemandRepository repository;

  /*
   * Alana, se a gente precisar fazer com que o
   * UserService chame métodos do DemandService (User -> Demand),
   * o Spring lançará um erro de Dependência Circular (Circular Dependency Error).
   * Se isso acontecer um dia, a solução geralmente é extrair a lógica que os dois
   * precisam para um terceiro Service, ou fazer a orquestração diretamente na
   * camada do Controller
   */
  @Autowired
  private UserService userService;

  @Transactional
  public Demand save(Demand demand) {

    demand.setEnabled(Boolean.TRUE);
    return repository.save(demand);
  }

  public List<Demand> getAll() {

    return repository.findAll();
  }

  public Demand getById(@NonNull Long id) {

    return repository.findById(id).get();
  }

  @Transactional
  public void update(@NonNull Long id, Demand demandChanged) {

    Demand demand = repository.findById(id).get();
    demand.setCode(demandChanged.getCode());
    demand.setTitle(demandChanged.getTitle());
    demand.setDescription(demandChanged.getDescription());
    demand.setImgUrl(demandChanged.getImgUrl());
    demand.setAddressId(demandChanged.getAddressId());
    demand.setCategoryId(demandChanged.getCategoryId());
    demand.setClientId(demandChanged.getClientId());
    demand.setDemandStatus(demandChanged.getDemandStatus());
    demand.setProfessionalId(demandChanged.getProfessionalId());

    repository.save(demand);
  }

  @Transactional
  public Demand reassign(@NonNull Long demandId, Long newProfessionalId) {
    Demand demand = repository.findById(demandId)
        .orElseThrow(() -> new NoSuchElementException("Demanda não encontrada"));

    // Regra de negócio: Só pode reatribuir se estiver rejeitada
    // (ou aberta, dependendo da regra que a gente deixar em vigor, blz?)
    if (demand.getDemandStatus() != DemandStatus.REJECTED) {
      throw new IllegalStateException("Apenas demandas rejeitadas podem ser reatribuídas.");
    }

    User newProfessional = userService.getById(newProfessionalId);

    // Agora atualiza o prof e coloca o status como OPEN de novo
    demand.setProfessionalId(newProfessional);
    demand.setDemandStatus(DemandStatus.OPENED);

    return repository.save(demand);
  }

  @Transactional
  public void delete(@NonNull Long id) {

    Demand demand = repository.findById(id).get();
    demand.setEnabled(Boolean.FALSE);

    repository.save(demand);
  }
}
