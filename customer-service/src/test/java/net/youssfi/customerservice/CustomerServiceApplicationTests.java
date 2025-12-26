package net.youssfi.customerservice;

import net.youssfi.customerservice.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
@org.springframework.test.context.ActiveProfiles("test")
public class CustomerServiceApplicationTests {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15")
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

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetAllCustomers() {
        var response = restTemplate.exchange(
                "http://localhost:" + port + "/customers",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Customer>>() {}
        );

        var customers = response.getBody();
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
        var customer = restTemplate.getForObject(
                "http://localhost:" + port + "/customers/1",
                Customer.class
        );

        assertThat(customer).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo("Mohammadi");
        assertThat(customer.getLastName()).isEqualTo("Imane");
        assertThat(customer.getEmail()).isEqualTo("imane@gmail.com");
    }
}