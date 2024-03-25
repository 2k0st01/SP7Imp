package ua.kiev.prog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// DB -> JDBC -> JPA(H): E..E -> DAO..DAO / Repository -> S..S -> C...C -> DTO -> View / React / Vue

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("/WEB-INF/static/");
    }

    @Bean
    public CommandLineRunner demo(final ContactService contactService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                Group group = new Group("Test");
                Group group2 = new Group("Test2");
                Contact contact;

                contactService.addGroup(group);
                contactService.addGroup(group2);

                for (int i = 0; i < 13; i++) {
                    contact = new Contact(null, "Name" + i, "Surname" + i, "1234567" + i, "user" + i + "@test.com");
                    contactService.addContact(contact);
                }
                for (int i = 0; i < 10; i++) {
                    contact = new Contact(group, "Other" + i, "OtherSurname" + i, "7654321" + i, "user" + i + "@other.com");
                    contactService.addContact(contact);
                }
                for (int i = 0; i < 10; i++) {
                    contact = new Contact(group2, "Other2" + i, "OtherSurname" + i, "7654321" + i, "user" + i + "@other.com");
                    contactService.addContact(contact);
                }
            }
        };
    }
}
