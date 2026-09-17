package br.com.conectaPro.model.payment;

public enum PaymentStatus {
  PENDENTE, // Cobrança criada, aguardando o cliente pagar/simular o pagamento
  APROVADO, // Pagamento aprovado, dinheiro "repassado" (simulado por enquanto)
  RECUSADO, // Pagamento recusado; o cliente pode gerar uma nova cobrança
  ESTORNADO // Pagamento devolvido após ter sido aprovado
}
