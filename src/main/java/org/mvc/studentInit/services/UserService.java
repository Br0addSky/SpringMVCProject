package org.mvc.studentInit.services;

import lombok.Data;
import org.mvc.studentInit.model.Role;
import org.mvc.studentInit.model.User;
import org.mvc.studentInit.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void adminInit() {
        if (userRepository.findByEmail("Admin@admin") == null) {
            User user = new User();
            user.setName("Admin");
            user.setSurname("Admin");
            user.setEmail("Admin@admin");
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.SUPER_USER));
            user.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(user);
        }
    }

    public void addUserInModel(Model model) {
        User user = new User();
        model.addAttribute("user", user);

    }

    public void adduser(User user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Пользователь существует");
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);


    }

    public void replaceUsers(Model model) {
        model.addAttribute("users", userRepository.findActiveUsers());
    }

    public void userEdit(Model model, User user) {
        model.addAttribute("user2", user);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("roles", Role.values());

    }

    public void userSave(Map<String, String> userRolesMap, User user) {
        Set<String> roleSet = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();
        for (String key : userRolesMap.keySet()) {
            if (roleSet.contains(key))
                user.getRoles().add(Role.valueOf(key));
        }
        userRepository.save(user);

    }

    public List<User> searchUsers(String filterName, String filterSurname,
                                  Model model) {
        if (filterName != null && !filterName.isBlank() || filterSurname != null && !filterSurname.isBlank()) {
            model.addAttribute("message", "Найденные пользователи");
            return userRepository.findByNameAndSurnameContains(filterName, filterSurname);
        } else {
            model.addAttribute("message", "Пользователь не найден");
            return userRepository.findAll();
        }

    }

    public void findInactiveUsers(Model model) {
        if (!userRepository.findInactiveUsers().isEmpty()) {
            model.addAttribute("users", userRepository.findInactiveUsers());
            model.addAttribute("message", "Найденные пользователи");
            return;
        }
        replaceUsers(model);
        model.addAttribute("message", "Неактивные пользователи не найдены");
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s);
    }
}
