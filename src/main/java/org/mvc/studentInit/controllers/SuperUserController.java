package org.mvc.studentInit.controllers;

import org.mvc.studentInit.model.User;
import org.mvc.studentInit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/users/superUserPage")
@PreAuthorize("hasAuthority('SUPER_USER')")
public class SuperUserController {
    private final UserService userService;

    @Autowired
    public SuperUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userList(Model model) {
        userService.replaceUsers(model);
        return "users/superUserPage";
    }

    @GetMapping("{user}")
    public String userEdit(@PathVariable User user, Model model) {
        userService.userEdit(model, user);
        return "users/userEdit";
    }

    @PostMapping("/toUserPage")
    public String toUserPage() {
        return "redirect:/users/userPage";
    }

    @PostMapping("/save")
    public String userSave(@RequestParam Map<String, String> form,
                           @RequestParam("userId") User user) {
        userService.userSave(form, user);
        return "redirect:/users/superUserPage";
    }

    @PostMapping("/searchUser")
    public String searchUsers(@RequestParam String filterName, Model model,
                              @RequestParam String filterSurname) {
        model.addAttribute("users", userService.searchUsers(filterName, filterSurname, model));
        return "users/superUserPage";
    }

    @PostMapping("/inactiveUsers")
    public String searchUnActiveUsers(Model model) {
        userService.findInactiveUsers(model);
        return "users/superUserPage";
    }



}
