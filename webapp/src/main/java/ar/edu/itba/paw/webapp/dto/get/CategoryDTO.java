package ar.edu.itba.paw.webapp.dto.get;

import ar.edu.itba.paw.models.Category;
import org.springframework.context.MessageSource;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Locale;

public class CategoryDTO {
    private String description;
    private long id;

    private URI url;
    private URI imageUrl;

    public static CategoryDTO fromCategory(Category category, UriInfo uri, MessageSource messageSource, Locale locale) {
        CategoryDTO toReturn = new CategoryDTO();
        toReturn.description = messageSource.getMessage(category.getDescription(), null, locale);
        toReturn.id = category.getId();
        toReturn.url = uri.getBaseUriBuilder().path("categories").path(String.valueOf(category.getId())).build();
        toReturn.imageUrl = uri.getBaseUriBuilder().path("images").path(String.valueOf(category.getPicture().getId())).build();
        return toReturn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URI getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}