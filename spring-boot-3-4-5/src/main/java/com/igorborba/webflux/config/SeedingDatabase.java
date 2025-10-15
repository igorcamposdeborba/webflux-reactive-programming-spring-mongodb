package com.igorborba.webflux.config;

import java.time.Instant;
import java.util.Arrays;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.igorborba.webflux.entities.Post;
import com.igorborba.webflux.entities.User;
import com.igorborba.webflux.repositories.PostRepository;
import com.igorborba.webflux.repositories.UserRepository;

// Banco de dados temporário para testes: cada vez que executa, exclui os registros
@Configuration
public class SeedingDatabase implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Override
	public void run(String... args) throws Exception {

		userRepository.deleteAll()
				.thenMany(postRepository.deleteAll())
				.thenMany(userRepository.saveAll(Arrays.asList(
						new User(null, "Maria Brown", "maria@gmail.com"),
						new User(null, "Alex Green", "alex@gmail.com"),
						new User(null, "Bob Grey", "bob@gmail.com")
				)))
				.collectList()
				.flatMapMany(users -> {
					User maria = users.get(0);
					User alex = users.get(1);
					User bob = users.get(2);

					Post post1 = new Post(new ObjectId().toHexString(),
							Instant.parse("2022-11-21T18:35:24.00Z"),
							"Partiu viagem",
							"Vou viajar para São Paulo. Abraços!",
							maria.getId(),
							maria.getName());

					Post post2 = new Post(new ObjectId().toHexString(),
							Instant.parse("2022-11-23T17:30:24.00Z"),
							"Bom dia",
							"Acordei feliz hoje!",
							maria.getId(),
							maria.getName());

					post1.addComment("Boa viagem mano!", Instant.parse("2022-11-21T18:52:24.00Z"), alex.getId(), alex.getName());
					post1.addComment("Aproveite!", Instant.parse("2022-11-22T11:35:24.00Z"), bob.getId(), bob.getName());

					post2.addComment("Tenha um ótimo dia!", Instant.parse("2022-11-23T18:35:24.00Z"), alex.getId(), alex.getName());

					return postRepository.saveAll(Arrays.asList(post1, post2));
				})
				.subscribe();
	}

}
