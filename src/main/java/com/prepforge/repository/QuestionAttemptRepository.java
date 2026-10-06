package com.prepforge.repository;
import com.prepforge.entity.QuestionAttempt; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt,Long>{
 List<QuestionAttempt> findByUserId(Long userId); Optional<QuestionAttempt> findByUserIdAndQuestionId(Long u,Long q);
 long countByUserIdAndSolvedTrue(Long u);
}
