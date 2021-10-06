package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.models.DBImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    Optional<DBImage> findById(long id);

    DBImage create(MultipartFile img);
}