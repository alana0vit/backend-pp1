package br.com.conectaPro.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.conectaPro.dto.CoordinatesDTO;
import br.com.conectaPro.model.user.AddressUser;

@Service
public class GeoLocationService {

    private final RestTemplate restTemplate;

    @Value("${geoapify.url}")
    private String url;

    @Value("${geoapify.key}")
    private String apiKey;

    public GeoLocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CoordinatesDTO getCoordinates(AddressUser address) {

        try {

            String query = String.format(
                    "%s, %s, %s, %s, %s, %s, Brasil",
                    address.getStreet(),
                    address.getNumber(),
                    address.getNeighborhood(),
                    address.getCity(),
                    address.getState(),
                    address.getZipCode());

            String requestUrl = url +
                    "?text=" + URLEncoder.encode(query, StandardCharsets.UTF_8) +
                    "&apiKey=" + apiKey;

            System.out.println("URL GEO: " + requestUrl);

            String response = restTemplate.getForObject(requestUrl, String.class);

            System.out.println("RESPOSTA GEO: " + response);

            System.out.println("QUERY GEOAPIFY: " + query);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            JsonNode features = root.path("features");

            if (!features.isArray() || features.isEmpty()) {
                throw new RuntimeException("Nenhuma coordenada encontrada");
            }

            JsonNode props = features.get(0).path("properties");

            CoordinatesDTO dto = new CoordinatesDTO();
            dto.setLat(props.get("lat").asText());
            dto.setLon(props.get("lon").asText());

            return dto;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao geocodificar: " + e.getMessage());
        }
    }
}