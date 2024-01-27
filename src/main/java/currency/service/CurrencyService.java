package currency.service;

import lombok.RequiredArgsConstructor;
import currency.client.EcbContentWebClient;
import currency.model.CurrencyResponseModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final EcbContentWebClient ecbContentWebClient;

    public BigDecimal rateCurrencyToEur(String currency, String volume) {
        CurrencyResponseModel<?> currencyResponseModel = ecbContentWebClient.getContent();
        if (currencyResponseModel.content() instanceof HashMap) {

            @SuppressWarnings("unchecked")
            Map<String, String> currencyRates = (Map<String, String>) currencyResponseModel.content();

            String currencyValue = currencyRates.get(currency.toUpperCase());

            if (currencyValue != null) {
                return new BigDecimal(volume)
                        .divide(new BigDecimal(currencyValue), 2, RoundingMode.HALF_UP);
            }
        }
        return null;
    }
}
