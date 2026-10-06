package com.prepforge.repository;
import com.prepforge.entity.InterviewExperience; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface InterviewExperienceRepository extends JpaRepository<InterviewExperience,Long>{List<InterviewExperience> findByCompanyId(Long id);}
