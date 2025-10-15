package com.igorborba.webflux.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
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

	@GetMapping
	public Flux<UserDTO> findAll() {
		Flux<UserDTO> users = service.findAll();
		return users;
	}

	@GetMapping(value = "/{id}")
	public Mono<ResponseEntity<UserDTO>> findById(@PathVariable String id) {
		Mono<UserDTO> user = service.findById(id);
		return user.map(userDTO -> ResponseEntity.ok().body(userDTO));
	}

	@PostMapping
	public Mono<ResponseEntity<UserDTO>> insert(@RequestBody UserDTO dto) {
		Mono<UserDTO> userDTO = service.insert(dto);

		URI uri = UriComponentsBuilder.fromPath("/users/{id}").buildAndExpand(userDTO.map(UserDTO::getId)).toUri();
		return userDTO.map(user -> ResponseEntity.created(uri).body(user));
	}

	@PutMapping(value = "/{id}")
	public Mono<ResponseEntity<UserDTO>> update(@PathVariable String id, @RequestBody UserDTO dto) {
		Mono<UserDTO> user = service.update(id, dto);
		return user.map(ResponseEntity::ok);
	}

	@DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id).then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

//	@GetMapping(value = "/{id}/posts")
//	public ResponseEntity<List<PostDTO>> findPosts(@PathVariable String id) {
//		Flux<PostDTO> list = service.findPosts(id);
//		return list;
//	}


//
//	@PutMapping(value = "/{id}")
//	public ResponseEntity<UserDTO> update(@PathVariable String id, @RequestBody UserDTO dto) {
//		dto = service.update(id, dto);
//		return ResponseEntity.ok(dto);
//	}
//
//	@DeleteMapping(value = "/{id}")
//    public ResponseEntity<Void> delete(@PathVariable String id) {
//        service.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}
