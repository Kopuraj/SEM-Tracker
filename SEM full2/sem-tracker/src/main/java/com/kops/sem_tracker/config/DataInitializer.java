package com.kops.sem_tracker.config;

import com.kops.sem_tracker.entyties.Subject;
import com.kops.sem_tracker.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedSubjects(SubjectRepository subjectRepository) {
        return args -> {
            if (subjectRepository.count() == 0) {
                subjectRepository.save(new Subject("Mathematics", "MATH101", 100, 50));
                subjectRepository.save(new Subject("Physics", "PHYS101", 100, 50));
                subjectRepository.save(new Subject("Chemistry", "CHEM101", 100, 50));
                subjectRepository.save(new Subject("Computer Science", "CS101", 100, 50));
            }
        };
    }
}


