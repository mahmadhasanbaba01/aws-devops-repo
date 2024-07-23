package com.vz.gydv.planning.qualification.controller;

import com.vividsolutions.jts.geom.Geometry;
import com.vz.gydv.planning.qualification.annotation.Clients;
import com.vz.gydv.planning.qualification.annotation.ResponseCodes;
import com.vz.gydv.planning.qualification.dto.model.filter.PolygonFilter;
import com.vz.gydv.planning.qualification.dto.model.geometry.*;
import com.vz.gydv.planning.qualification.dto.model.qualification.BuildingsResponseDTO;
import com.vz.gydv.planning.qualification.dto.model.search.FilteredSearchInput;
import com.vz.gydv.planning.qualification.dto.model.search.QualificationGroupSearchInput;
import com.vz.gydv.planning.qualification.dto.model.search.QualificationSearchInput;
import com.vz.gydv.planning.qualification.enumerator.Client;
import com.vz.gydv.planning.qualification.exception.BadRequestException;
import com.vz.gydv.planning.qualification.service.NewQualificationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 *
 * @author Justin Wheeler
 *
 */
@RestController
@RequestMapping("/5g/qualification")
public class QualificationController {

    @Autowired
    private NewQualificationService newQualificationService;


    /**
     * Searches Qualification
     *
     * @param criteria The search criteria to use.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a paginated result set of Buildings found.
     */
    @Clients(value = {Client.BCS, Client.UI})
    @ResponseCodes
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Finds a Qualification",
                  notes = "Searches a Qualification by the Criteria Provided",
                  response = QualificationDTO.class)
	public Object qualification(@RequestBody QualificationSearchInput criteria,
                                HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        QualificationDTO<Geometry> qualification = newQualificationService.qualification(criteria);
        if(qualification != null){
            qualification.setCriteria(criteria);
        }
        return qualification;
	}

    @Clients(value = {Client.BCS, Client.UI})
    @ResponseCodes
    @RequestMapping(value = "/getvendorKeys", method = RequestMethod.POST)
    @ApiOperation(value = "Finds a Qualification",
            notes = "Searches a Qualification by the Criteria Provided",
            response = QualificationDTO.class)
    public Object getVendorKeys(@RequestBody QualificationSearchInput criteria,
                                HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        Set<String> vendorKeys = newQualificationService.getVendorKeys(criteria);
        if(vendorKeys== null){
            throw new BadRequestException("Invalid Criteria");
        }
        return vendorKeys;
    }
    /**
     * Aggregates Exposure data for Eligible Buildings.
     *
     * @param criteria The search criteria to use.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return the Coverage Map Aggregation.
     */
    @Clients(value = {Client.MF, Client.POS})
    @ResponseCodes
    @RequestMapping(value = "/coverage/map", method = RequestMethod.POST)
    @ApiOperation(value = "Provides Aggregate Coverage",
            notes = "Provides Aggregate Coverage based on Criteria Provided for 'best' signal only",
            response = CoverageMapResponseDTO.class)
    public Object coverageMap(@RequestBody QualificationSearchInput criteria,
                              HttpServletRequest httpRequest,
                              HttpServletResponse httpResponse){
            return newQualificationService.coverageMap(criteria);

    }
    /**
     * Aggregates Exposure data for Eligible Buildings.
     *
     * @param criteria The search criteria to use.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return the Coverage Map Aggregation.
     */
    @ResponseCodes
    @RequestMapping(value = "/polygon", method = RequestMethod.POST)
    @ApiOperation(value = "Searches Entities by Polygon",
                  notes = "Searches Entities by Polygon",
                  response = PolygonDTO.class)
    public Object polygon(@RequestBody PolygonFilter criteria,
                          @RequestParam(required = false) Integer page,
                          @RequestParam(required = false) Integer size,
                          HttpServletRequest httpRequest,
                          HttpServletResponse httpResponse){

        int pg = page != null && page > 0 ? page - 1 : 0;
        int sz = size != null && size > 0 ? size : 50;

        return newQualificationService.polygonSearch(criteria, new PageRequest(pg, sz), httpResponse);
    }
    /**
     * Searches Any/All Entities based on Criteria
     *
     * @param criteria The search criteria to use.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a paginated result set of Any/All entities found.
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseCodes
    @ApiOperation(value = "Searches Grouped Entities",
                  notes = "Searches Grouped Entities by the Criteria Provided",
                  response = QualificationGroupDTO.class, responseContainer = "List")
    public Object search(@RequestBody QualificationGroupSearchInput criteria,
                         HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        Integer page = criteria.getPagination().getPage();
        Integer size = criteria.getPagination().getSize();

        criteria.getPagination().setPage(page != null && page > 0 ? page - 1 : 0);
        criteria.getPagination().setSize(size != null && size > 0 ? size : 50);

        return newQualificationService.search(criteria, httpResponse);

    }
    /**
     * Searches Buildings
     *
     * @param criteria The search criteria to use.
     * @param page The page of records to pull.
     * @param size The number of records to pull per page.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a paginated result set of Buildings found.
     */
    @Clients(value = {Client.UI})
    @ResponseCodes
    @RequestMapping(value = "/search/buildings", method = RequestMethod.POST)
    @ApiOperation(value = "Searches Buildings",
                  notes = "Searches Buildings by the Criteria Provided",
                  response = BuildingDTO.class, responseContainer = "List")
    public Object searchBuildings(@RequestBody FilteredSearchInput criteria,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer size,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        int pg = page != null && page > 0 ? page - 1 : 0;
        int sz = size != null && size > 0 ? size : 50;

        PageRequest pageable = new PageRequest(pg, sz);



        return newQualificationService.buildingSearch(criteria, pageable, httpResponse);

    }

    /**
     * Searches Buildings
     *
     * @param criteria The search criteria to use.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a list of Buildings found.
     */
    @Clients(value = {Client.UI})
    @ResponseCodes
    @RequestMapping(value = "/search/buildingsByAddr", method = RequestMethod.POST)
    @ApiOperation(value = "Get Buildings",
            notes = "Get building list by the Criteria Provided",
            response = BuildingsResponseDTO.class, responseContainer = "List")
    public Object searchBuildingList(@RequestBody QualificationSearchInput criteria,
                                     HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        BuildingsResponseDTO buildingsResponseDTO = newQualificationService.getBuildingListByAddress(criteria);
        return buildingsResponseDTO;

    }
    /**
     * Searches Coverages
     *
     * @param criteria The search criteria to use.
     * @param page The page of records to pull.
     * @param size The number of records to pull per page.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a paginated result set of Coverages found.
     */
    @Clients(value = {Client.UI})
    @ResponseCodes
    @RequestMapping(value = "/search/coverages", method = RequestMethod.POST)
    @ApiOperation(value = "Searches Coverages",
                  notes = "Searches Coverages by the Criteria Provided",
                  response = CoverageDTO.class, responseContainer = "List")
    public Object searchCoverages(@RequestBody FilteredSearchInput criteria,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer size,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        int pg = page != null && page > 0 ? page - 1 : 0;
        int sz = size != null && size > 0 ? size : 10;

        PageRequest pageable = new PageRequest(pg, sz);

        return newQualificationService.coverageSearch(criteria, pageable, httpResponse);

    }

    /**
     * Searches Descriptors
     *
     * @param criteria The search criteria to use.
     * @param page The page of records to pull.
     * @param size The number of records to pull per page.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a paginated result set of Descriptors and their corresponding Buildings found.
     */
    @Clients(value = {Client.UI})
    @ResponseCodes
    @RequestMapping(value = "/search/descriptors", method = RequestMethod.POST)
    @ApiOperation(value = "Searches Descriptors",
                  notes = "Searches Descriptors by the Criteria Provided",
                  response = BuildingDTO.class, responseContainer = "List")
    public Object searchDescriptors(@RequestBody FilteredSearchInput criteria,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        int pg = page != null && page > 0 ? page - 1 : 0;
        int sz = size != null && size > 0 ? size : 50;

        PageRequest pageable = new PageRequest(pg, sz);


        return newQualificationService.descriptorSearch(criteria, pageable, httpResponse);

    }
    /**
     * Searches Orders
     *
     * @param criteria The search criteria to use.
     * @param page The page of records to pull.
     * @param size The number of records to pull per page.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a paginated result set of Orders found.
     */
    @Clients(value = {Client.MTAS, Client.UI})
    @ResponseCodes
    @RequestMapping(value = "/search/orders", method = RequestMethod.POST)
    @ApiOperation(value = "Searches Orders",
                  notes = "Searches Orders by the Criteria Provided",
                  response = OrderDTO.class, responseContainer = "List")
    public Object searchOrders(@RequestBody FilteredSearchInput criteria,
                               @RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer size,
                               HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        int pg = page != null && page > 0 ? page - 1 : 0;
        int sz = size != null && size > 0 ? size : 50;

        PageRequest pageable = new PageRequest(pg, sz);


        return newQualificationService.orderSearch(criteria, pageable, httpResponse);


    }
    /**
     * Searches Prequals
     *
     * @param criteria The search criteria to use.
     * @param page The page of records to pull.
     * @param size The number of records to pull per page.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a paginated result set of Prequals found.
     */
    @Clients(value = {Client.BCS, Client.UI})
    @ResponseCodes
    @RequestMapping(value = "/search/prequals", method = RequestMethod.POST)
    @ApiOperation(value = "Searches Prequals",
                  notes = "Searches Prequals by the Criteria Provided",
                  response = PrequalDTO.class, responseContainer = "List")
    public Object searchPrequals(@RequestBody FilteredSearchInput criteria,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer size,
                                 HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        int pg = page != null && page > 0 ? page - 1 : 0;
        int sz = size != null && size > 0 ? size : 50;

        PageRequest pageable = new PageRequest(pg, sz);


        return newQualificationService.prequalSearch(criteria, pageable, httpResponse);

    }
    /**
     * Searches Sites
     *
     * @param criteria The search criteria to use.
     * @param page The page of records to pull.
     * @param size The number of records to pull per page.
     * @param httpRequest The HttpServletRequest for user authorization.
     * @param httpResponse The HttpServletResponse to append header information.
     * @return a paginated result set of Sites found.
     */
    @Clients(value = {Client.UI})
    @ResponseCodes
    @RequestMapping(value = "/search/sites", method = RequestMethod.POST)
    @ApiOperation(value = "Searches Sites",
            notes = "Searches Sites by the Criteria Provided",
            response = SiteDTO.class, responseContainer = "List")
    public Object searchSites(@RequestBody FilteredSearchInput criteria,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer size,
                              HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        int pg = page != null && page > 0 ? page - 1 : 0;
        int sz = size != null && size > 0 ? size : 50;

        PageRequest pageable = new PageRequest(pg, sz);

        return newQualificationService.siteSearch(criteria, pageable, httpResponse);

    }



}
