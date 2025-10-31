package com.firefist.rag_chat_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "ApiKeyAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-API-KEY" // or whichever header you use in your app; match your config
)
@Configuration
@OpenAPIDefinition(
        security = @SecurityRequirement(name = "ApiKeyAuth"),
        info = @Info(
                title = "RAG Chat Service API",
                version = "1.0.0",
                description = "APIs for retrieval-augmented generation chat service",
                contact = @Contact(name = "Vikas Rawat", email = "vikasalex1996@gmail.com"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        )
)
public class SwaggerConfig { }
