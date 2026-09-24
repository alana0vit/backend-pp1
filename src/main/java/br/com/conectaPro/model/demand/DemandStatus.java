package br.com.conectaPro.model.demand;

public enum DemandStatus {
  // aberto (enviou pro profissional), aguardando_pagamento (profissional aceitou e definiu o
  // valor final, esperando o cliente pagar), aguardando (pagamento aprovado, profissional esta
  // fazendo ou ja fez), fechado (avaliou) e rejeitado (caso ele nao queira fazer)
  ABERTO, // Ao ser enviada ao profissional
  AGUARDANDO_PAGAMENTO, // Profissional aceitou e definiu o valor final; aguardando pagamento
  AGUARDANDO, // Pagamento aprovado, profissional está fazendo ou já fez,
  FECHADO, // Feita e avaliada
  REJEITADO, // Rejeitada, caso ele não queira fazer, notifica o usuário e pode voltar ficar como
  // aberta?
  EXPIRADO // Profissional não respondeu em 1 hora e a demanda pode ser reatribuída
}
