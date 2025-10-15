package com.igorborba.webflux.controllers;

import com.igorborba.webflux.controllers.util.URL;
import com.igorborba.webflux.dto.PostDTO;
import com.igorborba.webflux.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.Instant;

@RestController
@RequestMapping(value = "/posts")
public class PostController {

    @Autowired
    private PostService service;

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<PostDTO>> findById(@PathVariable String id) {
        Mono<PostDTO> postDTO = service.findById(id);

        return postDTO.map(post -> ResponseEntity.ok(post));
    }

	@GetMapping(value = "/titlesearch")
	public Flux<PostDTO> findByTitle(@RequestParam(value = "text", defaultValue = "") String text) throws UnsupportedEncodingException {
		text = URL.decodeParam(text);
		Flux<PostDTO> posts = service.findByTitle(text);
		return posts;
	}

	@GetMapping(value = "/fullsearch")
	public Flux<PostDTO> fullSearch(
			@RequestParam(value = "text", defaultValue = "") String text,
			@RequestParam(value = "minDate", defaultValue = "") String minDate,
			@RequestParam(value = "maxDate", defaultValue = "") String maxDate) throws UnsupportedEncodingException, ParseException {

		text = URL.decodeParam(text);
		Instant min = URL.convertDate(minDate, Instant.EPOCH);
		Instant max = URL.convertDate(maxDate, Instant.now());

		Flux<PostDTO> list = service.fullSearch(text, min, max);
		return list;
	}
}
