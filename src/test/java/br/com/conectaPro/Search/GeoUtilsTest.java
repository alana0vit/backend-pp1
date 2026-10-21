package br.com.conectaPro.Search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import br.com.conectaPro.util.GeoUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeoUtilsTest {

  @Test
  @DisplayName("1 grau de longitude na linha do equador é ~111,19 km")
  void umGrauNoEquador() {
    assertEquals(111.19, GeoUtils.distanceKm(0, 0, 0, 1), 0.05);
  }

  @Test
  @DisplayName("Pontos idênticos dão distância 0 (sem NaN nem exceção)")
  void pontosIdenticos() {
    double d = GeoUtils.distanceKm(-8.0476, -34.877, -8.0476, -34.877);

    assertFalse(Double.isNaN(d));
    assertEquals(0.0, d, 0.0001);
  }

  @Test
  @DisplayName("Pontos antípodas não geram NaN")
  void pontosAntipodas() {
    double d = GeoUtils.distanceKm(0, 0, 0, 180);

    assertFalse(Double.isNaN(d));
    assertEquals(20015.09, d, 1.0);
  }
}
