package org.example.exam.controller;

import org.example.exam.model.placeholder;
import org.example.exam.service.placeholderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class placeholderController {
    @Autowired
    private placeholderService placeholderService;

    @GetMapping
    public List<placeholder> findAll() {
        return placeholderService.findAll();
    }

    @GetMapping("/{id}")
    public placeholder findById(@PathVariable int id) {
        return placeholderService.findById(id);
    }

    @PostMapping
    public placeholder create(@RequestBody placeholder placeholder) {
        return placeholderService.save(placeholder);
    }

    @PutMapping("/{id}")
    public placeholder update(@PathVariable int id, @RequestBody placeholder placeholder) {
        return placeholderService.update(id, placeholder);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        placeholderService.delete(id);
    }
}
