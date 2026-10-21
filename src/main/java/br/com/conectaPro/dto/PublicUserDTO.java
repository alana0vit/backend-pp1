package br.com.conectaPro.dto;

import br.com.conectaPro.dto.UserResponseDTO.CategoryBasicDTO;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import br.com.conectaPro.util.GeoUtils;
import java.util.List;

/**
 * Perfil público de um usuário (o que qualquer pessoa logada pode ver de outra). Mantém os mesmos
 * nomes de chave do UserResponseDTO que as telas de listagem já leem (inclusive "adresses"), mas
 * SEM e-mail, telefone, CPF/CNPJ, data de nascimento e sem rua/número/CEP. Coordenadas arredondadas
 * (3 casas, ~110 m).
 */
public record PublicUserDTO(
    Long id,
    String name,
    String photo,
    Double rating,
    UserType userType,
    Boolean verified,
    String activePlanName,
    List<CategoryBasicDTO> categories,
    List<PublicAddressDTO> adresses) {

  public record PublicAddressDTO(
      Long id, String neighborhood, String city, String state, Double latitude, Double longitude) {}

  public static PublicUserDTO fromEntity(User user) {
    List<CategoryBasicDTO> categories =
        user.getCategories() == null
            ? List.of()
            : user.getCategories().stream()
                .map(c -> new CategoryBasicDTO(c.getId(), c.getName()))
                .toList();

    List<PublicAddressDTO> addresses =
        user.getAdresses() == null
            ? List.of()
            : user.getAdresses().stream()
                .map(
                    a ->
                        new PublicAddressDTO(
                            a.getId(),
                            a.getNeighborhood(),
                            a.getCity(),
                            a.getState(),
                            GeoUtils.round(a.getLatitude(), 3),
                            GeoUtils.round(a.getLongitude(), 3)))
                .toList();

    return new PublicUserDTO(
        user.getId(),
        user.getName(),
        user.getPhoto(),
        user.getRating(),
        user.getUserType(),
        user.getVerified(),
        user.getActivePlanName(),
        categories,
        addresses);
  }
}
