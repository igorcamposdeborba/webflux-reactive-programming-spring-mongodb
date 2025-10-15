package com.igorborba.webflux.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.igorborba.webflux.dto.PostDTO;
import com.igorborba.webflux.repositories.PostRepository;
import com.igorborba.webflux.services.exceptions.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PostService {

	@Autowired
	private PostRepository repository;

	public Mono<PostDTO> findById(String id) {
		return repository.findById(id)
						 .map(PostDTO::new)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Post não encontrado: " + id)));
	}

	public Flux<PostDTO> findByTitle(String text) {
		Flux<PostDTO> postDTO = repository.searchTitle(text).map(PostDTO::new);
		return postDTO;
	}

	public Flux<PostDTO> fullSearch(String text, Instant minDate, Instant maxDate) {
		maxDate = maxDate.plusSeconds(86400); // 24 * 60 * 60
		Flux<PostDTO> result = repository.fullSearch(text, minDate, maxDate).map(PostDTO::new);
		return result;
	}
}
