package br.com.conectaPro.model.demand;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserService;
import br.com.conectaPro.security.EmailService;
import jakarta.transaction.Transactional;

@Service
public class DemandService {

    @Autowired
    private DemandRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Demand save(Demand demand) {
        demand.setEnabled(Boolean.TRUE);
        demand.setOpenedAt(LocalDateTime.now()); // marca quando foi aberta para o cron

        Demand saved = repository.save(demand);

        // Notifica o profissional sobre a nova solicitação
        try {
            emailService.notificarNovaDemanda(saved);
        } catch (Exception e) {
            System.err.println("Erro ao notificar nova demanda: " + e.getMessage());
        }

        return saved;
    }

    public List<Demand> getAll() {
        return repository.findAll();
    }

    public Demand getById(@NonNull Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Demanda não encontrada com ID: " + id));
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

        if (demandChanged.getImgUrl() != null) {
            demand.setImgUrl(demandChanged.getImgUrl());
        }
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

        // Permite reatribuição tanto de REJEITADO quanto de EXPIRADO
        if (demand.getDemandStatus() != DemandStatus.REJEITADO
                && demand.getDemandStatus() != DemandStatus.EXPIRADO) {
            throw new IllegalStateException(
                    "Apenas demandas rejeitadas ou expiradas podem ser reatribuídas.");
        }

        User newProfessional = userService.getById(newProfessionalId);
        demand.setProfessionalId(newProfessional);
        demand.setDemandStatus(DemandStatus.ABERTO);
        demand.setOpenedAt(LocalDateTime.now()); // reinicia o contador de 1 hora

        Demand saved = repository.save(demand);

        // Notifica o novo profissional
        try {
            emailService.notificarNovaDemanda(saved);
        } catch (Exception e) {
            System.err.println("Erro ao notificar reatribuição: " + e.getMessage());
        }

        return saved;
    }

    @Transactional
    public Demand updateStatus(Long id, DemandStatus status) {
        Demand demand = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Demanda não encontrada."));

        DemandStatus currentStatus = demand.getDemandStatus();

        if (status == DemandStatus.ABERTO) {
            throw new IllegalStateException(
                    "Não é permitido reabrir uma demanda por este endpoint. Use /reassign.");
        }

        switch (currentStatus) {
            case ABERTO:
                if (status != DemandStatus.AGUARDANDO && status != DemandStatus.REJEITADO) {
                    throw new IllegalStateException("Transição inválida: ABERTO → " + status);
                }
                break;

            case AGUARDANDO:
                if (status != DemandStatus.FECHADO) {
                    throw new IllegalStateException("Transição inválida: AGUARDANDO → " + status);
                }
                break;

            case FECHADO:
                throw new IllegalStateException("Não é possível alterar o status de uma demanda fechada.");

            case REJEITADO:
                throw new IllegalStateException(
                        "Não é permitido alterar o status desta demanda por este endpoint. Use /reassign.");

            case EXPIRADO:
                throw new IllegalStateException(
                        "Não é permitido alterar o status desta demanda por este endpoint. Use /reassign.");
        }

        demand.setDemandStatus(status);
        Demand saved = repository.save(demand);

        try {
            switch (status) {
                case AGUARDANDO -> emailService.notificarDemandaAceita(saved);
                case REJEITADO  -> emailService.notificarDemandaRejeitada(saved);
                case FECHADO    -> emailService.notificarDemandaFechada(saved);
                default -> {}
            }
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação de status [" + status + "]: " + e.getMessage());
        }

        return saved;
    }

    @Transactional
    public void delete(@NonNull Long id) {
        Demand demand = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Demanda não encontrada com ID: " + id));
        demand.setEnabled(Boolean.FALSE);
        repository.save(demand);
    }
}