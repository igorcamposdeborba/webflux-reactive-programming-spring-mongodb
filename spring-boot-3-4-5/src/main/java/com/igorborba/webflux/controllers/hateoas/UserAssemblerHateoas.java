package com.igorborba.webflux.controllers.hateoas;
import com.igorborba.webflux.controllers.UserController;
import com.igorborba.webflux.dto.UserDTO;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

// HATEOAS - links para endpoints relacionados. Este é o padrão mais alto do RESTFul segundo Martin Fawler
@Component
public class UserAssemblerHateoas {

    public EntityModel<UserDTO> toModel(UserDTO userDTO){
        return EntityModel.of(userDTO,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel(), // autorelacionamento (link para o recurso atual de findAll)
                linkTo(methodOn(UserController.class).findById(userDTO.getId())).withRel("GET - user by id"), // link relacionado para findById
                linkTo(methodOn(UserController.class).insert(userDTO)).withRel("POST - add user"),
                linkTo(methodOn(UserController.class).update(userDTO.getId(), userDTO)).withRel("PUT - update user attribute"),
                linkTo(methodOn(UserController.class).delete(userDTO.getId())).withRel("DELETE - delete user by id")
        );
    }

}
