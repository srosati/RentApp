package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.OrderOptions;
import ar.edu.itba.paw.webapp.dto.get.OrderOptionDTO;
import ar.edu.itba.paw.webapp.utils.ApiUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@Path("orderOptions")
public class OrderOptionsController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private MessageSource messageSource;

    @Inject
    private javax.inject.Provider<ContainerRequest> requestProvider;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list() {
        Locale lang = ApiUtils.resolveLocale(requestProvider.get().getAcceptableLanguages());

        final List<OrderOptionDTO> orderOptions = Arrays.stream(OrderOptions.values()).map(option ->
                OrderOptionDTO.fromOrderOption(option, uriInfo, messageSource, lang)).collect(Collectors.toList());

        if (orderOptions.isEmpty())
            return Response.noContent().build();

        return Response.ok(new GenericEntity<List<OrderOptionDTO>>(orderOptions) {}).build();
    }
}