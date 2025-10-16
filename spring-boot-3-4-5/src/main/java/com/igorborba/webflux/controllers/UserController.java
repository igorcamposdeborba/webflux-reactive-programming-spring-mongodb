package com.igorborba.webflux.controllers;

import java.net.URI;

import com.igorborba.webflux.controllers.hateoas.UserAssemblerHateoas;
import com.igorborba.webflux.dto.PostDTO;
import com.igorborba.webflux.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.igorborba.webflux.dto.UserDTO;
import com.igorborba.webflux.services.UserService;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService service;

	@Autowired
	private PostService postService;

	@Autowired
	private UserAssemblerHateoas userAssemblerHateoas;

	@GetMapping
	public Flux<EntityModel<UserDTO>> findAll() {
		Flux<UserDTO> users = service.findAll(); // Flux: fluxo de dados (Collection - exemplo: lista) - vem do Spring webflux (programação reativa e concorrente)
		return users.map(userAssemblerHateoas::toModel);
	}

	@GetMapping(value = "/{id}")
	public Mono<EntityModel<UserDTO>> findById(@PathVariable String id) {
		Mono<UserDTO> user = service.findById(id); // Mono: um único dado - vem do Spring webflux (programação reativa e concorrente)
		return user.map(actualUser -> userAssemblerHateoas.toModel(actualUser));
	}

	@PostMapping
	public Mono<ResponseEntity<EntityModel<UserDTO>>> insert(@RequestBody UserDTO dto) {
		Mono<UserDTO> userDTO = service.insert(dto);

		URI uri = UriComponentsBuilder.fromPath("/users/{id}").buildAndExpand(userDTO.map(UserDTO::getId)).toUri();
		return userDTO.map(user -> ResponseEntity.created(uri)
										   .body(userAssemblerHateoas.toModel(user))); // ResponseEntity mantido para poder personalizar o status para created() e colocar o body como DTO
	}																				   // Hateoas (links relacionados) aplicados neste endpoint

	@PutMapping(value = "/{id}")
	public Mono<EntityModel<UserDTO>> update(@PathVariable String id, @RequestBody UserDTO dto) {
		Mono<UserDTO> user = service.update(id, dto);
		return user.map(userAssemblerHateoas::toModel); // Não coloquei ResponseEntity porque não vou personalizar o status padrão de 200 ok() para o endpoint de update porque ele é padrão 200 ok()
	}

	@DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id).then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

	@GetMapping("/{id}/posts")
	public Flux<ResponseEntity<PostDTO>> getPostsByUserId(@PathVariable String id){
		Mono<UserDTO> user = service.findById(id);
		return user.flatMapMany(actualUser -> postService.findByUserId(actualUser.getId()))
																 .map(ResponseEntity::ok);
	}
}
