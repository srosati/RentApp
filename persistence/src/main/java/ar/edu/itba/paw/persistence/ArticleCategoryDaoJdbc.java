package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.ArticleCategoryDao;
import ar.edu.itba.paw.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ArticleCategoryDaoJdbc implements ArticleCategoryDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private static final RowMapper<Category> ROW_MAPPER =
            (resultSet, rowNum) -> new Category(
                    resultSet.getInt("id"),
                    resultSet.getString("description")
            );

    @Autowired
    public ArticleCategoryDaoJdbc(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("article_category")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public List<Category> findFromArticle(long articleId) {
        return jdbcTemplate.query("SELECT * FROM category WHERE id IN(" +
                        "SELECT DISTINCT category_id FROM article_category WHERE article_id = ?)",
                new Object[] {articleId}, ROW_MAPPER);
    }

    @Override
    public Category addToArticle(long articleId, Category category) {
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("category_id", category.getId());
        categoryData.put("article_id",articleId);
        jdbcInsert.execute(categoryData);
        return category;
    }
}
