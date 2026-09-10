package br.com.conectaPro.dto;

import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    String phone,
    LocalDate birthDate,
    UserType userType,
    String registryId,
    Double rating,
    String photo,
    List<CategoryBasicDTO> categories,
    List<AddressBasicDTO> adresses) {

  public record CategoryBasicDTO(Long id, String name) {}

  public record AddressBasicDTO(
      Long id,
      String street,
      String number,
      String neighborhood,
      String city,
      String state,
      String zipCode,
      String supplement,
      Double latitude,
      Double longitude) {}

  public static UserResponseDTO fromEntity(User user) {
    List<CategoryBasicDTO> categoryDTOs =
        user.getCategories() == null
            ? List.of()
            : user.getCategories().stream()
                .map(c -> new CategoryBasicDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());

    List<AddressBasicDTO> addressDTOs =
        user.getAdresses() == null
            ? List.of()
            : user.getAdresses().stream()
                .map(
                    a ->
                        new AddressBasicDTO(
                            a.getId(),
                            a.getStreet(),
                            a.getNumber(),
                            a.getNeighborhood(),
                            a.getCity(),
                            a.getState(),
                            a.getZipCode(),
                            a.getSupplement(),
                            a.getLatitude(),
                            a.getLongitude()))
                .collect(Collectors.toList());

    return new UserResponseDTO(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPhone(),
        user.getBirthDate(),
        user.getUserType(),
        user.getRegistryId(),
        user.getRating(),
        user.getPhoto(),
        categoryDTOs,
        addressDTOs);
  }
}
