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

    Demand demand = repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Solicitação não encontrada."));

    if (demand.getDemandStatus() != DemandStatus.ABERTO) {
      throw new IllegalStateException(
          "Não é permitido editar um serviço aceito ou finalizado!");
    }

    demand.setTitle(demandChanged.getTitle());
    demand.setDescription(demandChanged.getDescription());
    demand.setImgUrl(demandChanged.getImgUrl());

    if (demandChanged.getAddressId() != null) {
      demand.setAddressId(demandChanged.getAddressId());
    }

    if (demandChanged.getCategoryId() != null) {
      demand.setCategoryId(demandChanged.getCategoryId());
    }

    if (demandChanged.getProfessionalId() != null) {
      demand.setProfessionalId(demandChanged.getProfessionalId());
    }

    repository.save(demand);
  }

  @Transactional
  public Demand reassign(@NonNull Long demandId, Long newProfessionalId) {
    Demand demand = repository.findById(demandId)
        .orElseThrow(() -> new NoSuchElementException("Demanda não encontrada"));

    // Regra de negócio: Só pode reatribuir se estiver rejeitada
    // (ou aberta, dependendo da regra que a gente deixar em vigor, blz?)
    if (demand.getDemandStatus() != DemandStatus.REJEITADO) {
      throw new IllegalStateException("Apenas demandas rejeitadas podem ser reatribuídas.");
    }

    User newProfessional = userService.getById(newProfessionalId);

    // Agora atualiza o prof e coloca o status como OPEN de novo
    demand.setProfessionalId(newProfessional);
    demand.setDemandStatus(DemandStatus.ABERTO);

    return repository.save(demand);
  }

  @Transactional
  public Demand updateStatus(Long id, DemandStatus status) {
    Demand demand = repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Demanda não encontrada."));

    DemandStatus currentStatus = demand.getDemandStatus();

    // Impede qualquer alteração para ABERTO via updateStatus, apenas pelo endpoint reassing()
    if (status == DemandStatus.ABERTO && currentStatus == DemandStatus.REJEITADO) {
      throw new IllegalStateException(
          "Não é permitido reabrir uma demanda rejeitada pelo endpoint de status. Use /reassign.");
    }

    switch (currentStatus) {
      case ABERTO:
        if (status != DemandStatus.AGUARDANDO && status != DemandStatus.REJEITADO) {
          throw new IllegalStateException("Transição inválida");
        }
        break;

      case AGUARDANDO:
        if (status != DemandStatus.FECHADO) {
          throw new IllegalStateException("Transição inválida");
        }
        break;

      case FECHADO:
        throw new IllegalStateException("Não é possível alterar este status");

      case REJEITADO:
        throw new IllegalStateException("Não é permitido alterar o status desta demanda por este endpoint");
    }

    demand.setDemandStatus(status);

    return repository.save(demand);
  }

  @Transactional
  public void delete(@NonNull Long id) {

    Demand demand = repository.findById(id).get();
    demand.setEnabled(Boolean.FALSE);

    repository.save(demand);
  }
}
