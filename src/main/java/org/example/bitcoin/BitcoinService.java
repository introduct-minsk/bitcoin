package org.example.bitcoin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.net.URIBuilder;
import org.example.bitcoin.model.CurrentRate;
import org.example.bitcoin.model.History;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;

public class BitcoinService {

    private final ObjectMapper mapper;

    public BitcoinService() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
    }

    public BigDecimal getCurrentRate(@NonNull String currency) throws IOException, URISyntaxException {
        currency = formatAndValidateCurrency(currency);

        URI uri = new URI("https://api.coindesk.com/v1/bpi/currentprice/" + currency + ".json");

        CurrentRate currentRate = getApiResponse(uri, CurrentRate.class);
        return currentRate.getBpi().get(currency).getRate();
    }

    public Map<LocalDate, BigDecimal> getHistory(@NonNull String currency, @NonNull LocalDate start, @NonNull LocalDate end) throws IOException, URISyntaxException {
        currency = formatAndValidateCurrency(currency);

        URI uri = new URIBuilder("https://api.coindesk.com/v1/bpi/historical/close.json")
                .addParameter("start", start.toString())
                .addParameter("end", end.toString())
                .addParameter("currency", currency)
                .build();

        History history = getApiResponse(uri, History.class);
        return history.getBpi();
    }

    private String formatAndValidateCurrency(String currency) {
        currency = currency.trim().toUpperCase();
        if (!currency.matches("[A-Z]+")) {
            throw new IllegalArgumentException();
        }
        return currency;
    }

    private <T> T getApiResponse(URI uri, Class<T> responseType) throws IOException {
        HttpGet request = new HttpGet(uri);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            if (response.getCode() == 404) {
                throw new IllegalArgumentException();
            }

            if (response.getEntity() != null) {
                return mapper.readValue(response.getEntity().getContent(), responseType);
            }
            throw new IllegalStateException();
        }
    }
}
