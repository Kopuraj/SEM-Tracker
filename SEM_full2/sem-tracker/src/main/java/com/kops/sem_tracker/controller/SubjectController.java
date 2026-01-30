package com.kops.sem_tracker.controller;

import com.kops.sem_tracker.entyties.Subject;
import com.kops.sem_tracker.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<Subject> create(@RequestBody Subject subject) {
        // Check if subject code already exists
        if (subjectService.subjectCodeExists(subject.getSubjectCode())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(subjectService.save(subject));
    }

    @GetMapping
    public ResponseEntity<List<Subject>> getAll() {
        return ResponseEntity.ok(subjectService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getById(@PathVariable Long id) {
        Optional<Subject> subject = subjectService.getById(id);
        return subject.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{subjectCode}")
    public ResponseEntity<Subject> getBySubjectCode(@PathVariable String subjectCode) {
        Optional<Subject> subject = subjectService.getBySubjectCode(subjectCode);
        return subject.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> update(@PathVariable Long id, @RequestBody Subject newData) {
        return ResponseEntity.ok(subjectService.update(id, newData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
