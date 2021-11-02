package org.mvc.studentInit.controllers;

import org.mvc.studentInit.model.User;
import org.mvc.studentInit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/home")
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String login() {
        return "home/login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        userService.addUserInModel(model);
        return "home/registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "home/registration";
        }
        try{
            userService.adduser(user);
        } catch (IllegalArgumentException ex){
            model.addAttribute("message", ex.getMessage());
            return "home/registration";
        }
        return "redirect:/home";
    }
}
