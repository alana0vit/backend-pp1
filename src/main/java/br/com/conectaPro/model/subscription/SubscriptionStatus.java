package br.com.conectaPro.model.subscription;

public enum SubscriptionStatus {
  PENDENTE, // Assinatura criada, aguardando pagamento
  ATIVA, // Pagamento aprovado, dentro do período contratado
  EXPIRADA, // Passou de expiresAt sem renovação
  CANCELADA // Profissional cancelou antes do fim do período
}
