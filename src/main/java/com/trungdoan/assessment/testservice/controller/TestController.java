package com.trungdoan.assessment.testservice.controller;

import com.trungdoan.assessment.testservice.model.UserTest;
import com.trungdoan.assessment.testservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class TestController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping("/users")
    public List<UserTest> list() {
        return userRepository.findAll();
    }
}
