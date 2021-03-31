package org.example.bitcoin;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class Application {

    public static void main(String[] args) throws Exception {

        System.out.print("Please enter currency code: ");
        Scanner in = new Scanner(System.in);
        String currency = in.nextLine().toUpperCase();

        BitcoinService service = new BitcoinService();

        try {
            BigDecimal currentPrice = service.getCurrentRate(currency);
            System.out.println("The current Bitcoin rate: " + currentPrice + " " + currency);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid currency code: " + currency);
            System.exit(1);
        }

        Map<LocalDate, BigDecimal> history = service.getHistory(currency, LocalDate.now().minusDays(30), LocalDate.now());
        BigDecimal min = history.values().stream().min(BigDecimal::compareTo).orElse(null);
        BigDecimal max = history.values().stream().max(BigDecimal::compareTo).orElse(null);
        System.out.println("The lowest Bitcoin rate in the last 30 days: " + min + " " + currency);
        System.out.println("The highest Bitcoin rate in the last 30 days: " + max + " " + currency);
    }
}
