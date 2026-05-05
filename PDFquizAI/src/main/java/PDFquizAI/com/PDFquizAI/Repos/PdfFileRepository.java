package PDFquizAI.com.PDFquizAI.Repos;




import PDFquizAI.com.PDFquizAI.Entites.PdfFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PdfFileRepository extends JpaRepository<PdfFile, Long> {

    List<PdfFile> findByUserId(Long userId);
}
