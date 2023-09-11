package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(StudentRepository studentRepository, MongoTemplate mongoTemplate) {
		return args -> {
			Address address = new Address(
					"VietNam",
					"Hanoi"
			);
			String email = "hoainam.nv44@gmail.com";
			Student student = new Student(
				"Nam",
				"Vo",
				email,
				Gender.MALE,
				address,
				List.of("Computer Science"),
				BigDecimal.TEN,
				LocalDateTime.now()
			);

//			usingMongoTemplateAndQuery(studentRepository, mongoTemplate, email, student);

			studentRepository.findStudentByEmail(email).ifPresentOrElse(s->{
				System.out.println(s + " already exists");
			},  ()-> {
				System.out.println("inserting student " + student);
				studentRepository.insert(student);
			});
		};
	}

	private static void usingMongoTemplateAndQuery(StudentRepository studentRepository, MongoTemplate mongoTemplate, String email, Student student) {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(email));
		List<Student> students = mongoTemplate.find(query, Student.class);

		if(students.size() > 1) {
			throw new IllegalStateException("Found many students with email " + email);
		}

		if(students.isEmpty()) {
			System.out.println("inserting student " + student);
			studentRepository.insert(student);
		}else {
			System.out.println(student + " already exists");
		}
	}
}
