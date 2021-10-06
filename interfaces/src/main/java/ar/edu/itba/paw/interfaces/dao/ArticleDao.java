package ar.edu.itba.paw.interfaces.dao;

import ar.edu.itba.paw.models.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleDao {

    List<Article> filter(String name, Long category, String orderBy, Long user, Long location, long page);

    List<Article> rentedArticles(long renterId, long page);

    Optional<Article> findById(long id);

    Article createArticle(String title, String description, Float pricePerDay, long idOwner);

    int editArticle(long id, String title, String description, Float pricePerDay);

    Long getMaxPage(String name, Long category, Long user, Long location);

    Long getRentedMaxPage(Long user);

    List<Article> recommendedArticles(long articleId);

    Long timesRented(long articleId);

}