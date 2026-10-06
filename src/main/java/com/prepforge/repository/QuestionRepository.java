package com.prepforge.repository;
import com.prepforge.entity.Question; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface QuestionRepository extends JpaRepository<Question,Long>{
 List<Question> findByCompanyId(Long companyId); List<Question> findByCategoryIgnoreCase(String category);
 List<Question> findByTopicIgnoreCase(String topic); List<Question> findByQuestionTextContainingIgnoreCase(String q);
}
