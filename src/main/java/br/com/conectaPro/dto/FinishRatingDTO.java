package br.com.conectaPro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinishRatingDTO {

  private Boolean approved;

  private Integer points;

  private String description;

  private Boolean anonymous;
}
