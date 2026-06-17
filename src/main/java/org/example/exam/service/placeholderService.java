package org.example.exam.service;

import org.example.exam.model.placeholder;
import org.example.exam.repository.placeholderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class placeholderService {

    @Autowired
    private placeholderRepository placeholderRepository;

    public List<placeholder> findAll() {
        return placeholderRepository.findAll();
    }

    public placeholder findById(int id) {
        return placeholderRepository.findById(id).orElse(null);
    }

    public placeholder save(placeholder placeholder) {
        return placeholderRepository.save(placeholder);
    }

    public placeholder update(int id, placeholder placeholder) {
        placeholder.setId(id);
        return placeholderRepository.save(placeholder);
    }

    public void delete(int id) {
        placeholderRepository.deleteById(id);
    }
}