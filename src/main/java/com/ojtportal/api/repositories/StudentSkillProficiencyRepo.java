package com.ojtportal.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojtportal.api.entity.Skill;
import com.ojtportal.api.entity.Student;
import com.ojtportal.api.entity.StudentSkillProficiency;

@Repository
public interface StudentSkillProficiencyRepo extends JpaRepository<StudentSkillProficiency, Integer> {
    StudentSkillProficiency findBySkillAndStudent(Skill skill, Student student);
    List<StudentSkillProficiency> findByStudent(Student student);
}
