package com.example.onlyflans;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "api.mercadopago.access-token=TEST_TOKEN_SIMULADO",
        "wa.token=TOKEN_SIMULADO_WHATSAPP",
        "wa.phone.number.id=123456789"
})
class OnlyFlansApplicationTests {

    @Test
    void contextLoads() {
        // Valida que el contenedor de Spring Boot cargue exitosamente con H2
    }
}