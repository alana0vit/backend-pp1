package br.com.conectaPro.model.demand;

public enum DemandStatus {
    // aberto (enviou pro profissional), aguardando (profissional aceitou e esta fazendo ou ja fez), fechado (avaliou) e rejeitado (caso ele nao queira fazer)
    ABERTO, // Ao ser enviada ao profissional
    AGURADANDO, // Profissional aceitou, está fazendo ou já fez, 
    FECHADO, // Feita e avaliada
    REJEITADO // Rejeitada, caso ele não queira fazer, notifica o usuário e pode voltar ficar como aberta?
}
