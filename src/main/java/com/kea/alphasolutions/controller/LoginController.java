package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.repository.ResourceRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final ResourceRepository resourceRepository;

    public LoginController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        
        Resource resource = resourceRepository.findByUsername(username);
        
        if (resource != null && resource.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", resource);
            return "redirect:/projects";
        }
        
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "error/403";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                          @RequestParam String role,
                          @RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        
        if (resourceRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        
        Resource newUser = new Resource();
        newUser.setName(name);
        newUser.setRole(role);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setSystemRole("USER");
        
        resourceRepository.save(newUser);
        
        Resource savedUser = resourceRepository.findByUsername(username);
        session.setAttribute("loggedInUser", savedUser);
        
        return "redirect:/projects";
    }
}