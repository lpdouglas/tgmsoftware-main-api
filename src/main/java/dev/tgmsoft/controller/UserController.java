package dev.tgmsoft.controller;

import dev.tgmsoft.model.UserModel;
import dev.tgmsoft.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<UserModel> users() {
        return userRepository.findAll();
    }
}

