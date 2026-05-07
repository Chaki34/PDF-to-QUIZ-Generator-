package PDFquizAI.com.PDFquizAI.Repos;



import PDFquizAI.com.PDFquizAI.Entites.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository
        extends JpaRepository<QuizAttempt, Long> {

    // 🔥 Get recent attempts for dashboard
    List<QuizAttempt> findTop10ByUserIdOrderBySubmittedAtDesc(Long userId);

}
