package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.Subject;
import com.kops.sem_tracker.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getById(Long id) {
        return subjectRepository.findById(id);
    }

    public Optional<Subject> getBySubjectCode(String subjectCode) {
        return subjectRepository.findBySubjectCode(subjectCode);
    }

    public Subject update(Long id, Subject newData) {
        return subjectRepository.findById(id)
                .map(existing -> {
                    existing.setSubjectName(newData.getSubjectName());
                    existing.setSubjectCode(newData.getSubjectCode());
                    existing.setTotalMarks(newData.getTotalMarks());
                    existing.setPassMarks(newData.getPassMarks());
                    existing.setDescription(newData.getDescription());
                    return subjectRepository.save(existing);
                })
                .orElseGet(() -> {
                    newData.setId(id);
                    return subjectRepository.save(newData);
                });
    }

    public void delete(Long id) {
        subjectRepository.deleteById(id);
    }

    public boolean subjectCodeExists(String subjectCode) {
        return subjectRepository.existsBySubjectCode(subjectCode);
    }
}