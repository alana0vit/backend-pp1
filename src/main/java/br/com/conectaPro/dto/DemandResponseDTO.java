package br.com.conectaPro.dto;

import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandStatus;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Resposta de demanda. Mantém as mesmas chaves da entidade (clientId/professionalId continuam sendo
 * OBJETOS de usuário), mas o usuário aninhado vem sem CPF/CNPJ, nascimento e endereços, e o contato
 * (telefone e e-mail) só aparece depois que o pagamento foi aprovado.
 */
public record DemandResponseDTO(
    Long id,
    String code,
    String title,
    String description,
    List<String> imgUrl,
    Double suggestedValue,
    Double finalValue,
    LocalDate suggestedDate,
    LocalDateTime openedAt,
    AddressUser addressId,
    Category categoryId,
    PartyDTO clientId,
    PartyDTO professionalId,
    DemandStatus demandStatus) {

  /** Status em que o contato do outro lado é liberado (pagamento já aprovado). */
  public static final Set<DemandStatus> CONTACT_RELEASED =
      EnumSet.of(DemandStatus.AGUARDANDO, DemandStatus.FECHADO);

  /** Usuário aninhado na demanda. phone/email só vêm preenchidos com o contato liberado. */
  public record PartyDTO(
      Long id,
      String name,
      String photo,
      Double rating,
      UserType userType,
      Boolean verified,
      String activePlanName,
      String phone,
      String email) {}

  public static DemandResponseDTO fromEntity(Demand demand) {
    boolean contactReleased = CONTACT_RELEASED.contains(demand.getDemandStatus());

    return new DemandResponseDTO(
        demand.getId(),
        demand.getCode(),
        demand.getTitle(),
        demand.getDescription(),
        demand.getImgUrl() == null ? List.of() : new ArrayList<>(demand.getImgUrl()),
        demand.getSuggestedValue(),
        demand.getFinalValue(),
        demand.getSuggestedDate(),
        demand.getOpenedAt(),
        demand.getAddressId(),
        demand.getCategoryId(),
        party(demand.getClientId(), contactReleased),
        party(demand.getProfessionalId(), contactReleased),
        demand.getDemandStatus());
  }

  public static List<DemandResponseDTO> fromList(List<Demand> demands) {
    return demands.stream().map(DemandResponseDTO::fromEntity).toList();
  }

  private static PartyDTO party(User user, boolean contactReleased) {
    if (user == null) {
      return null;
    }
    return new PartyDTO(
        user.getId(),
        user.getName(),
        user.getPhoto(),
        user.getRating(),
        user.getUserType(),
        user.getVerified(),
        user.getActivePlanName(),
        contactReleased ? user.getPhone() : null,
        contactReleased ? user.getEmail() : null);
  }
}
