package com.igorborba.webflux.services;

import com.igorborba.webflux.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.igorborba.webflux.dto.UserDTO;
import com.igorborba.webflux.entities.User;
import com.igorborba.webflux.repositories.UserRepository;
import com.igorborba.webflux.services.exceptions.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	public Flux<UserDTO> findAll() {
		return userRepository.findAll().map(UserDTO::new);
	}

	public Mono<UserDTO> findById(String id) {
		return userRepository.findById(id).map(UserDTO::new)
									  .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuário não encontrado")));
	}
	public Mono<UserDTO> insert(UserDTO dto) {
		User user = new User();
		copyDtoToEntity(dto, user);
		Mono<User> userSaved = userRepository.insert(user);
		return userSaved.map(UserDTO::new)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Erro ao salvar no banco de dados: " + this)));
	}

	public Mono<UserDTO> update(String id, UserDTO dto) {
		return userRepository.findById(id).flatMap(user -> {
			user.setName(dto.getName());
			user.setEmail(dto.getEmail());
			return userRepository.save(user);
		}).map(user -> new UserDTO(user))
		.switchIfEmpty(Mono.error(new ResourceNotFoundException("Erro ao salvar no banco de dados: " + this)));
	}

	public Mono<Void> delete(String id) {
		return userRepository.findById(id)
				  .switchIfEmpty(Mono.error(new ResourceNotFoundException("Erro ao salvar no banco de dados: " + this)))
				  .flatMap(user -> userRepository.delete(user));
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
	}
}
