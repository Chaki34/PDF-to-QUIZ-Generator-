package PDFquizAI.com.PDFquizAI.Repos;

import PDFquizAI.com.PDFquizAI.Entites.GeneratedQuiz;
import PDFquizAI.com.PDFquizAI.Entites.QuizRoom;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneratedQuizRepository
        extends JpaRepository<GeneratedQuiz, Long> {

    Optional<GeneratedQuiz> findByRoom(QuizRoom room);

    // 🔥 FIXED DELETE QUERY
    @Transactional
    @Modifying
    @Query("DELETE FROM GeneratedQuiz g WHERE g.room = :room")
    void deleteByRoom(QuizRoom room);
}
