package org.example.bitcoin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BitcoinServiceTest {

    private static final String VALID_CURRENCY = " EUR ";

    private final BitcoinService service = new BitcoinService();

    @Test
    void getCurrentRate() throws Exception {
        BigDecimal price = service.getCurrentRate(VALID_CURRENCY);
        assertNotNull(price);
        assertTrue(price.compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "getCurrentRate invalid currency \"{0}\"")
    @MethodSource("invalidCurrencyProvider")
    void getCurrentRate_InvalidCurrency(String currency) {
        assertThrows(IllegalArgumentException.class, () -> service.getCurrentRate(currency));
    }

    @Test
    void getHistory() throws Exception {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now();
        Map<LocalDate, BigDecimal> history = service.getHistory(VALID_CURRENCY, start, end);
        assertNotNull(history);
        assertTrue(history.size() > 0);

        history.forEach((key, value) -> {
            assertTrue(key.isAfter(start) || key.isEqual(start));
            assertTrue(key.isBefore(end) || key.isEqual(end));
            assertTrue(value.compareTo(BigDecimal.ZERO) > 0);
        });
    }

    @ParameterizedTest(name = "getHistory invalid currency \"{0}\"")
    @MethodSource("invalidCurrencyProvider")
    void getHistory_InvalidCurrency(String currency) {
        assertThrows(IllegalArgumentException.class, () -> service.getHistory(currency, LocalDate.now().minusMonths(1), LocalDate.now()));
    }

    @Test
    void getHistory_InTheFuture() {
        assertThrows(IllegalArgumentException.class, () -> service.getHistory(VALID_CURRENCY, LocalDate.now().plusYears(1), LocalDate.now().plusYears(2)));
    }

    @Test
    void getHistory_TooFarInThePast() {
        assertThrows(IllegalArgumentException.class, () -> service.getHistory(VALID_CURRENCY, LocalDate.now().minusYears(101), LocalDate.now().minusYears(100)));
    }

    @Test
    void getHistory_InvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> service.getHistory(VALID_CURRENCY, LocalDate.now().plusDays(1), LocalDate.now().minusDays(1)));
    }

    static Stream<String> invalidCurrencyProvider() {
        return Stream.of("", "   ", "\t", "USD1", "ZZZ", "A".repeat(2000));
    }
}
