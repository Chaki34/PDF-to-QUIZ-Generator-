package PDFquizAI.com.PDFquizAI.Entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String password;

    private String googleId;
    private String pictureUrl;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    private LocalDateTime createdAt;

}
