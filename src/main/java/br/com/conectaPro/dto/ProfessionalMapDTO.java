package br.com.conectaPro.dto;

import br.com.conectaPro.dto.UserResponseDTO.CategoryBasicDTO;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.util.GeoUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Visão enxuta de um profissional para o mapa. De propósito NÃO contém e-mail, telefone, CPF/CNPJ,
 * data de nascimento nem rua/número/CEP: o contato só deve ser liberado depois do aceite e do
 * pagamento da demanda. As coordenadas saem arredondadas (3 casas, ~110 m) para não revelar a porta
 * exata; a distância é calculada com as coordenadas reais.
 */
public record ProfessionalMapDTO(
    Long id,
    String name,
    String photo,
    Double rating,
    Boolean verified,
    String activePlanName,
    List<CategoryBasicDTO> categories,
    Double distanceKm,
    List<LocationDTO> locations) {

  public record LocationDTO(
      Long addressId,
      String neighborhood,
      String city,
      String state,
      Double latitude,
      Double longitude,
      Double distanceKm) {}

  private record Ranked(int priority, ProfessionalMapDTO dto) {}

  /**
   * Converte e ordena: maior prioridade de plano primeiro, depois menor distância (sem distância
   * vai para o fim) e, por fim, o id. Sem origem (latitude/longitude nulas) não há distância.
   */
  public static List<ProfessionalMapDTO> fromUsers(
      List<User> users, Double originLat, Double originLon) {
    Comparator<Ranked> ordem =
        (a, b) -> {
          int porPlano = Integer.compare(b.priority(), a.priority());
          if (porPlano != 0) {
            return porPlano;
          }
          int porDistancia = compararDistancia(a.dto().distanceKm(), b.dto().distanceKm());
          if (porDistancia != 0) {
            return porDistancia;
          }
          return Long.compare(a.dto().id(), b.dto().id());
        };

    return users.stream()
        .map(u -> new Ranked(prioridade(u), from(u, originLat, originLon)))
        .sorted(ordem)
        .map(Ranked::dto)
        .toList();
  }

  private static ProfessionalMapDTO from(User user, Double originLat, Double originLon) {
    boolean temOrigem = originLat != null && originLon != null;

    List<AddressUser> enderecos = user.getAdresses() == null ? List.of() : user.getAdresses();
    List<LocationDTO> locations =
        enderecos.stream().map(a -> toLocation(a, temOrigem, originLat, originLon)).toList();

    Double maisPerto =
        locations.stream()
            .map(LocationDTO::distanceKm)
            .filter(Objects::nonNull)
            .min(Double::compare)
            .orElse(null);

    List<CategoryBasicDTO> categorias =
        user.getCategories() == null
            ? List.of()
            : user.getCategories().stream()
                .map(c -> new CategoryBasicDTO(c.getId(), c.getName()))
                .toList();

    return new ProfessionalMapDTO(
        user.getId(),
        user.getName(),
        user.getPhoto(),
        user.getRating(),
        user.getVerified(),
        user.getActivePlanName(),
        categorias,
        maisPerto,
        locations);
  }

  private static LocationDTO toLocation(
      AddressUser a, boolean temOrigem, Double originLat, Double originLon) {
    Double lat = a.getLatitude();
    Double lon = a.getLongitude();

    Double distancia = null;
    if (temOrigem && lat != null && lon != null) {
      distancia = GeoUtils.round(GeoUtils.distanceKm(originLat, originLon, lat, lon), 2);
    }

    return new LocationDTO(
        a.getId(),
        a.getNeighborhood(),
        a.getCity(),
        a.getState(),
        GeoUtils.round(lat, 3),
        GeoUtils.round(lon, 3),
        distancia);
  }

  private static int prioridade(User user) {
    return user.getPriorityWeight() == null ? 0 : user.getPriorityWeight();
  }

  private static int compararDistancia(Double x, Double y) {
    if (x == null && y == null) {
      return 0;
    }
    if (x == null) {
      return 1;
    }
    if (y == null) {
      return -1;
    }
    return Double.compare(x, y);
  }
}
