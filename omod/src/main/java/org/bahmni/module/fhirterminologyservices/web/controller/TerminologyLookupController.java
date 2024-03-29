package org.bahmni.module.fhirterminologyservices.web.controller;

import org.bahmni.module.fhirterminologyservices.api.TerminologyLookupService;
import org.bahmni.module.fhirterminologyservices.api.task.ValueSetTask;
import org.hl7.fhir.r4.model.ValueSet;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir2.model.FhirTask;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/terminologyServices")
public class TerminologyLookupController extends BaseRestController {

    public static final String FHIR2_R4_TASK_URI = "/ws/fhir2/R4/Task/";

    @Autowired
    private TerminologyLookupService terminologyLookupService;

    @Autowired
    private ValueSetTask valueSetTask;

    @RequestMapping(value = "/searchDiagnosis", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> searchDiagnosis(@RequestParam(value = "term") String searchTerm, @RequestParam Integer limit,
                                                  @RequestParam(required = false) String locale) {
        return new ResponseEntity<>(terminologyLookupService.searchConcepts(searchTerm, limit, locale), HttpStatus.OK);
    }

    @RequestMapping(value = "/searchTerminologyCodes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> searchTerminologyCodes(@RequestParam(value = "code") String terminologyCode, @RequestParam(value = "size") Integer pageSize,
                                                    @RequestParam Integer offset, @RequestParam(required = false) String locale) {
        ValueSet valueSet = terminologyLookupService.searchTerminologyCodes(terminologyCode, pageSize, offset, locale);
        return new ResponseEntity<>(getPageObject(valueSet), HttpStatus.OK);
    }

    private TSPageObject getPageObject(ValueSet valueSet){
        List<String> codes = valueSet.getExpansion().getContains().stream().map(item -> item.getCode()).collect(Collectors.toList());
        TSPageObject pageObject = new TSPageObject();
        pageObject.setTotal(valueSet.getExpansion().getTotal());
        pageObject.setCodes(codes);
        return pageObject;
    }
    @RequestMapping(value = "/getObservationValueSet", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getObservationValueSet(@RequestParam(value = "valueSetUrl") String valueSetUrl,
                                                         @RequestParam(required = false) String locale,
                                                         @RequestParam(value = "term", required = false) String searchTerm, @RequestParam(required = false) Integer limit) {
        return new ResponseEntity<>(terminologyLookupService.searchConcepts(valueSetUrl, locale, searchTerm, limit), HttpStatus.OK);
    }

    @RequestMapping(value = "/valueSet", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity valueSetToConceptSet(@RequestParam (value = "valueSetId") List<String> valueSetIds,
                                               @RequestParam(required = false) String locale,
                                               @RequestParam String conceptClass,
                                               @RequestParam String conceptDatatype,
                                               @RequestParam (required = false) String contextRoot) {
        FhirTask task = valueSetTask.getInitialTaskResponse(valueSetIds);
        valueSetTask.convertValueSetsToConceptsTask(valueSetIds, locale, conceptClass, conceptDatatype, contextRoot, task, Context.getUserContext());
        return new ResponseEntity(getFhirTaskUri(task), HttpStatus.ACCEPTED);
    }

    private String getFhirTaskUri(FhirTask task) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FHIR2_R4_TASK_URI).path(task.getUuid()).build().toUriString();
    }
}
