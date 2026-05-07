package PDFquizAI.com.PDFquizAI.Repos;

import PDFquizAI.com.PDFquizAI.Entites.QuizRoom;
import PDFquizAI.com.PDFquizAI.Entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRoomRepository extends JpaRepository<QuizRoom, Long> {

    Optional<QuizRoom> findByRoomId(String roomId);

    List<QuizRoom> findByUser(User user);
}