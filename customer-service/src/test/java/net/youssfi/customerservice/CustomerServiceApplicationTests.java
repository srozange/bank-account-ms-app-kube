package net.youssfi.customerservice;

import net.youssfi.customerservice.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@org.springframework.test.context.ActiveProfiles("test")
public class CustomerServiceApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testGetAllCustomers() {
        var customers = webTestClient.get()
                .uri("/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(customers).hasSize(2);
        assertThat(customers.get(0).getFirstName()).isEqualTo("Mohammadi");
        assertThat(customers.get(0).getLastName()).isEqualTo("Imane");
        assertThat(customers.get(0).getEmail()).isEqualTo("imane@gmail.com");

        assertThat(customers.get(1).getFirstName()).isEqualTo("Ismaili");
        assertThat(customers.get(1).getLastName()).isEqualTo("Aymane");
        assertThat(customers.get(1).getEmail()).isEqualTo("aymane@gmail.com");
    }

    @Test
    void testGetFirstCustomer() {
        var customer = webTestClient.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(customer).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo("Mohammadi");
        assertThat(customer.getLastName()).isEqualTo("Imane");
        assertThat(customer.getEmail()).isEqualTo("imane@gmail.com");
    }
}