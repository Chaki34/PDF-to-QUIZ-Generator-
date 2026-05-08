package PDFquizAI.com.PDFquizAI.Repos;



import PDFquizAI.com.PDFquizAI.Entites.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface QuizAttemptRepository
        extends JpaRepository<QuizAttempt, Long> {

    // Dashboard (user level)
    List<QuizAttempt> findTop10ByUserIdOrderBySubmittedAtDesc(Long userId);

    // Library (PDF level)
    List<QuizAttempt> findByPdfFileId(Long pdfFileId);
}
