package net.youssfi.customerservice;

import net.youssfi.customerservice.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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

    private RestClient restClient;

    @Test
    void testGetAllCustomers() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
        
        List<Customer> customers = restClient.get()
                .uri("/customers")
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<Customer>>() {});

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
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
                
        Customer customer = restClient.get()
                .uri("/customers/1")
                .retrieve()
                .body(Customer.class);

        assertThat(customer).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo("Mohammadi");
        assertThat(customer.getLastName()).isEqualTo("Imane");
        assertThat(customer.getEmail()).isEqualTo("imane@gmail.com");
    }
}