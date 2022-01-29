package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.dto.get.UserDTO;
import ar.edu.itba.paw.webapp.dto.post.NewUserDTO;
import ar.edu.itba.paw.webapp.dto.put.EditUserDTO;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response create(FormDataMultiPart body) {
        NewUserDTO userDto = NewUserDTO.fromMultipartData(body);
        final User user = us.register(userDto.getEmail(), userDto.getPassword(), userDto.getFirstName(),
                userDto.getLastName(), userDto.getLocation(), userDto.getImage(), userDto.isOwner(),
                uriInfo.getAbsolutePath().toString());
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getById(@PathParam("id") final long id) {
        final User user = us.findById(id).orElseThrow(UserNotFoundException::new);
        return Response.ok(UserDTO.fromUser(user, uriInfo)).build();
    }

    @DELETE // TODO chequear
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@webSecurity.checkIsSameUser(authentication, #id)")
    public Response deleteById(@PathParam("id") final long id) {
        us.delete(id);
        return Response.noContent().build();
    }

    @PUT // TODO chequear
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@webSecurity.checkIsSameUser(authentication, #id)")
    public Response modify(@PathParam("id") final long id, EditUserDTO userDTO) {
        us.update(id, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getLocation());
        return Response.ok().build();
    }

    // TODO: add endpoint to change password
    // Maybe PUT /users/{id}/password
}