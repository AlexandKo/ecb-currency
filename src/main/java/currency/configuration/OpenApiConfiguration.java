package currency.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenApiConfiguration {
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    @Primary
    SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties swaggerUiConfigProperties = new SwaggerUiConfigProperties();
        swaggerUiConfigProperties.setUseRootPath(true);
        swaggerUiConfigProperties.setDisableSwaggerDefaultUrl(true);
        swaggerUiConfigProperties.setEnabled(true);
        swaggerUiConfigProperties.setConfigUrl(contextPath + "/v3/api-docs/swagger-config");
        swaggerUiConfigProperties.setUrl(contextPath + "/v3/api-docs");
        return swaggerUiConfigProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("currency")
                        .version("1.0-SNAPSHOT")
                        .contact(new Contact()
                                .name("Alexey Kodin")
                                .email("aleksejs.kodins@gmail.com")));
    }
}
