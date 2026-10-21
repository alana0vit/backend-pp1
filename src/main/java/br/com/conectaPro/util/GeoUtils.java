package br.com.conectaPro.util;

public final class GeoUtils {

  private static final double EARTH_RADIUS_KM = 6371.0;

  private GeoUtils() {}

  /** Arredonda para N casas decimais; null continua null. */
  public static Double round(Double value, int decimals) {
    if (value == null) {
      return null;
    }
    double factor = Math.pow(10, decimals);
    return Math.round(value * factor) / factor;
  }

  /** Distância em km entre dois pontos (fórmula de Haversine). */
  public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

    // min(1.0, ...): erro de arredondamento pode deixar 'a' acima de 1 e gerar NaN no asin
    return 2 * EARTH_RADIUS_KM * Math.asin(Math.min(1.0, Math.sqrt(a)));
  }
}
