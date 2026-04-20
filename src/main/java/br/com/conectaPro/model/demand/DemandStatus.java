package br.com.conectaPro.model.demand;

public enum DemandStatus {
    // aberto (enviou pro profissional), aguardando (profissional aceitou e esta fazendo ou ja fez), fechado (avaliou) e rejeitado (caso ele nao queira fazer)
    OPENED, // Ao ser enviada ao profissional
    IN_WAITING, // Profissional aceitou, está fazendo ou já fez, 
    CLOSED, // Feita e avaliada
    REJECTED // Rejeitada, caso ele não queira fazer, notifica o usuário e pode voltar ficar como aberta?
}
