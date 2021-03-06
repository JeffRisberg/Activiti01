package com.company.jersey01.endpoints;

import com.company.common.FilterDesc;
import com.company.jersey01.models.Donor;
import com.company.jersey01.services.DonorService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("donors")
public class DonorEndpoint extends AbstractEndpoint {

    protected DonorService donorService;

    @Inject
    public DonorEndpoint(DonorService donorService) {
        this.donorService = donorService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(@PathParam("id") Integer id) {

        Donor data = donorService.getDonor(id);

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

        List<Donor> data = donorService.getDonors(limit, offset, filterDescs);
        long totalCount = donorService.getDonorsCount(filterDescs);

        return createEntityListResponse(data, totalCount, limit, offset, null);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Integer id) {

        Donor data = donorService.getDonor(id);

        return createDeleteResponse(data,null);
    }
}
