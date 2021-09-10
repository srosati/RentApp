package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.ArticleCategoryDao;
import ar.edu.itba.paw.interfaces.ArticleDao;
import ar.edu.itba.paw.interfaces.CategoryDao;
import ar.edu.itba.paw.models.Article;
import ar.edu.itba.paw.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class ArticleDaoJdbc implements ArticleDao {

    @Autowired
    ArticleCategoryDao articleCategoryDao;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<Article> ROW_MAPPER =
            (resultSet, rowNum) -> new Article(
                    resultSet.getLong("id"),
                    resultSet.getString("title"),
                    resultSet.getString("description"),
                    resultSet.getFloat("price_per_day"),
                    resultSet.getLong("owner_id")
            );

    @Autowired
    public ArticleDaoJdbc(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("article")
                .usingGeneratedKeyColumns("id");

    }

    @Override
    public List<Article> filter(String name) {
        System.out.println(name);
        List<Article> articles =
                jdbcTemplate.query(
                "SELECT * FROM article WHERE LOWER(title) like ?",
                new Object[]{"%" + name.toLowerCase() + "%"},
                ROW_MAPPER);

        articles.forEach(t -> t.setCategories(articleCategoryDao.findFromArticle(t.getId())));
        return articles;
    }

    @Override
    public List<Article> list() {
        return jdbcTemplate.query("SELECT * FROM article", ROW_MAPPER);
    }

    @Override
    public Optional<Article> findById(long id) {
        Optional<Article> optArticle = jdbcTemplate.query("SELECT * FROM article WHERE id = ?",
                                                            new Object[]{id}, ROW_MAPPER)
                                                .stream()
                                                .findFirst();

        if (!optArticle.isPresent())
            return Optional.empty();

        Article article = optArticle.get();
        article.setCategories(articleCategoryDao.findFromArticle(article.getId()));

        return Optional.of(article);
    }


    @Override
    public Optional<Article> createArticle(String title, String description, Float pricePerDay, long idOwner) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("price_per_day", pricePerDay);
        data.put("owner_id", idOwner);

        long articleId =  jdbcInsert.executeAndReturnKey(data).longValue();

        return Optional.of(new Article(articleId, title, description, pricePerDay, idOwner));
    }
}
