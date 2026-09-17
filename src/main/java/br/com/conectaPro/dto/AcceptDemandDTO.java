package br.com.conectaPro.dto;

public class AcceptDemandDTO {
  // Opcional: se não vier, usa o suggestedValue da demanda
  private Double finalValue;

  public Double getFinalValue() {
    return finalValue;
  }

  public void setFinalValue(Double finalValue) {
    this.finalValue = finalValue;
  }
}
