package org.example.exam.repository;

import org.example.exam.model.placeholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface placeholderRepository extends JpaRepository<placeholder, Integer> {
}