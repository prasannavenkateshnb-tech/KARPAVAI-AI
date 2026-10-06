package com.prepforge.controller;
import com.prepforge.repository.UserRepository; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/api/admin")
public class AdminController {private final UserRepository users; public AdminController(UserRepository u){users=u;} @GetMapping("/students/count") public Map<String,Long> count(){return Map.of("count",users.countByRole("STUDENT"));}}
