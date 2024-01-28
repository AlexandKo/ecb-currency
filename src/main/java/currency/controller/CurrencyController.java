package currency.controller;

import currency.client.EcbContentWebClient;
import currency.model.CurrencyResponseModel;
import currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/currency", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CurrencyController {
    private static final String DAILY_EXCHANGE_RATES_EXAMPLE_OBJECT = "{\"CHF\":\"0.9396\",\"MXN\":\"18.6582\",\"ZAR\":\"20.4264\",\"INR\":\"90.3690\",\"CNY\":\"7.7994\",\"THB\":\"38.679\",\"AUD\":\"1.6483\",\"ILS\":\"4.0057\",\"KRW\":\"1451.08\",\"JPY\":\"160.62\",\"PLN\":\"4.3775\",\"GBP\":\"0.85368\",\"IDR\":\"17131.94\",\"HUF\":\"386.95\",\"PHP\":\"61.280\",\"TRY\":\"32.9445\",\"ISK\":\"148.30\",\"HKD\":\"8.4933\",\"DKK\":\"7.4549\",\"USD\":\"1.0871\",\"CAD\":\"1.4607\",\"MYR\":\"5.1393\",\"BGN\":\"1.9558\",\"NOK\":\"11.3325\",\"RON\":\"4.9765\",\"SGD\":\"1.4570\",\"CZK\":\"24.748\",\"SEK\":\"11.3203\",\"NZD\":\"1.7801\",\"BRL\":\"5.3366\"}";

    private final EcbContentWebClient ecbContentWebClient;
    private final CurrencyService currencyService;

    @GetMapping(path = "/ecb/list")
    @Operation(description = "Exchange rates from European Central Bank", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = DAILY_EXCHANGE_RATES_EXAMPLE_OBJECT))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "Error during connect to resource"))})
    })
    public ResponseEntity<Object> fetchCurrenciesList() {
        CurrencyResponseModel<?> model = ecbContentWebClient.getContent();
        if (model.content() instanceof HashMap) {
            return ResponseEntity.ok(model.content());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(model.content());
    }

    @GetMapping(path = "/ecb/convert/currency")
    @Operation(description = "Convert currency to EUR", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "114.22"))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    public ResponseEntity<Object> convertToEur(@Parameter(content = {@Content(schema = @Schema(example = "PLN"))})
                                               @RequestParam String currency,
                                               @Parameter(content = {@Content(schema = @Schema(example = "500"))})
                                               @RequestParam String volume) {
        BigDecimal result = currencyService.convertCurrencyToEur(currency, volume);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(path = "/ecb/convert/euro")
    @Operation(description = "Convert from EUR", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "2188.75"))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    public ResponseEntity<Object> rateToEur(@Parameter(content = {@Content(schema = @Schema(example = "PLN"))})
                                            @RequestParam String currency,
                                            @Parameter(content = {@Content(schema = @Schema(example = "500"))})
                                            @RequestParam String volume) {
        BigDecimal result = currencyService.convertCurrencyFromEuro(currency, volume);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
