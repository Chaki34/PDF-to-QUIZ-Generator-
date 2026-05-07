package PDFquizAI.com.PDFquizAI.Services;



import PDFquizAI.com.PDFquizAI.Entites.QuizRoom;
import PDFquizAI.com.PDFquizAI.Entites.User;
import PDFquizAI.com.PDFquizAI.Repos.QuizRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizRoomService {

    private final QuizRoomRepository quizRoomRepository;

    public QuizRoomService(QuizRoomRepository quizRoomRepository) {
        this.quizRoomRepository = quizRoomRepository;
    }

    // CREATE ROOM
    public QuizRoom createRoom(QuizRoom room) {
        return quizRoomRepository.save(room);
    }

    // FIND ROOM
    public Optional<QuizRoom> getRoom(String roomId) {
        return quizRoomRepository.findByRoomId(roomId);
    }

    // USER ROOMS
    public List<QuizRoom> getUserRooms(User user) {
        return quizRoomRepository.findByUser(user);
    }

    // DESTROY ROOM
    public void destroyRoom(String roomId) {

        Optional<QuizRoom> optionalRoom =
                quizRoomRepository.findByRoomId(roomId);

        if (optionalRoom.isPresent()) {

            QuizRoom room = optionalRoom.get();

            room.setActive(false);

            quizRoomRepository.save(room);
        }
    }
}
