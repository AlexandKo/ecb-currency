package currency.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import currency.client.EcbContentWebClient;
import currency.model.CurrencyResponseModel;
import currency.service.CurrencyService;
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
    private static final String DAILY_PRICE_EXAMPLE_OBJECT = "{\"date\":\"2023-07-26\",\"petrolPrice\":[{\"Gotika\":1.587}],\"dieselPrice\":[{\"Gotika\":1.447}]}";

    private final EcbContentWebClient ecbContentWebClient;
    private final CurrencyService currencyService;

    @GetMapping(path = "/ecb/list")
    @Operation(description = "Exchange rates from European Central Bank", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = DAILY_PRICE_EXAMPLE_OBJECT))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "Error during connect to resource"))})
    })
    public ResponseEntity<Object> fetchCurrenciesList() {
        CurrencyResponseModel<?> model = ecbContentWebClient.getContent();
        if (model.content() instanceof HashMap) {
            return ResponseEntity.ok(model.content());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(model.content());
    }

    @GetMapping(path = "/ecb/rate")
    @Operation(description = "Convert currency to EUR", method = "GET")
    public ResponseEntity<Object> rateToEur(@RequestParam String currency, @RequestParam String volume) {
        BigDecimal result = currencyService.rateCurrencyToEur(currency, volume);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
