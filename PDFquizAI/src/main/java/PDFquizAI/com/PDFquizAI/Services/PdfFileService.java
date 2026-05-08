package PDFquizAI.com.PDFquizAI.Services;




import PDFquizAI.com.PDFquizAI.Entites.PdfFile;
import PDFquizAI.com.PDFquizAI.Repos.PdfFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PdfFileService {

    private final PdfFileRepository repository;

    public PdfFileService(PdfFileRepository repository) {
        this.repository = repository;
    }

    public PdfFile save(PdfFile file) {
        return repository.save(file);
    }

    public List<PdfFile> getUserFiles(Long userId) {
        return repository.findByUser_Id(userId);
    }
}