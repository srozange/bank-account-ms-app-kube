package net.youssfi.accountservice;

import net.youssfi.accountservice.clients.CustomerRestClient;
import net.youssfi.accountservice.entities.AccountType;
import net.youssfi.accountservice.entities.BankAccount;
import net.youssfi.accountservice.entities.Customer;
import net.youssfi.accountservice.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableFeignClients
public class AccountServiceApplication {

    private static final List<String> IDS = List.of("d59a473b-a28f-418d-81d8-561fa2a9d4ac", "6d210d2d-f96c-4a89-8eb4-1739ad841fde",
            "86239664-6a90-498a-9d76-0e23569d31c2", "247db895-ac26-46ba-b43b-fa9939a874a1");

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(AccountRepository accountRepository, CustomerRestClient customerRestClient){
        return args -> {
            while (true) {
                AtomicInteger index = new AtomicInteger(0);
                List<Customer> allCustomers = customerRestClient.getAllCustomers();
                if (allCustomers.isEmpty()) {
                    Thread.sleep(2000);
                    continue;
                }
                allCustomers.forEach(customer -> {
                    for (AccountType accountType:AccountType.values()){
                        BankAccount bankAccount = BankAccount.builder()
                                .id(IDS.get(index.getAndIncrement()))
                                .balance(Math.random()*80000)
                                .createdAt(LocalDate.now())
                                .type(accountType)
                                .currency("MAD")
                                .customerId(customer.getId())
                                .build();
                        accountRepository.save(bankAccount);
                    }
                });
                break;
            }
        };
    }

}
