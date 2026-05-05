package PDFquizAI.com.PDFquizAI.Entites;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pdf_files")
@Getter
@Setter
public class PdfFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String originalName;
    private String filePath;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    private Long userId; // link to user

    // getters & setters
}
