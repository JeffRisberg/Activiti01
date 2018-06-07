package com.company.jersey01.endpoints;

import com.company.common.FilterDesc;
import com.company.jersey01.models.Charity;
import com.company.jersey01.services.CharityService;
import com.fasterxml.jackson.databind.JsonNode;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.List;

@Path("charities")
public class CharityEndpoint extends AbstractEndpoint {

    protected CharityService charityService;

    @Inject
    public CharityEndpoint(CharityService charityService) {
        this.charityService = charityService;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(@PathParam("id") Integer id) {

        Charity data = charityService.getCharity(id);

        return createEntityResponse(data, null);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchList(
            @DefaultValue("50") @QueryParam("limit") int limit,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("") @QueryParam("sort") String sortStr,
            @Context UriInfo uriInfo) {

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        List<FilterDesc> filterDescs = this.parseFiltering(queryParams);

        List<Charity> data = charityService.getCharities(limit, offset, filterDescs);
        long totalCount = charityService.getCharitiesCount(filterDescs);

        return createEntityListResponse(data, totalCount, limit, offset, null);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String requestBody) {
        try {
            Charity charity = objectMapper.readValue(requestBody, Charity.class);
            JsonNode jsonNode = objectMapper.readValue(requestBody, JsonNode.class);

            System.out.println(requestBody);
            System.out.println(charity);
            System.out.println(jsonNode);

            System.out.println(jsonNode.get("id") != null);
            System.out.println(jsonNode.get("name") != null);
            System.out.println(jsonNode.get("ein") != null);
            System.out.println(jsonNode.get("website") != null);

            Charity tempCharity = new Charity(123, "Dog Foundation", "99-12345", "http://www.dogs.com");
            return Response.status(Response.Status.OK).entity(tempCharity).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Integer id) {

        Charity data = charityService.getCharity(id);

        return createDeleteResponse(data, null);
    }
}
