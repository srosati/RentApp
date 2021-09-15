package ar.edu.itba.paw.interfaces;

import ar.edu.itba.paw.models.Article;
import ar.edu.itba.paw.models.Category;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ArticleService {
    List<Article> get(String name, Long category, String orderBy, Long user);

    Optional<Article> createArticle(String title, String description, Float pricePerDay, List<Long> categories, List<MultipartFile> image, long idOwner);

    Optional<Article> findById(Integer articleId);
}
