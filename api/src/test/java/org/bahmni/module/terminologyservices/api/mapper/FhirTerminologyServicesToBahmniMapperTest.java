package org.bahmni.module.terminologyservices.api.mapper;


import org.hl7.fhir.r4.model.ValueSet;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;

import static org.junit.Assert.*;

public class FhirTerminologyServicesToBahmniMapperTest {

    @Test
    public void shouldMapFhirTerminologyContainsSetToResponseList() {
        ValueSet.ValueSetExpansionContainsComponent valueSetExpansionContainsComponent = new ValueSet.ValueSetExpansionContainsComponent();
        valueSetExpansionContainsComponent.setCode("195967001");
        valueSetExpansionContainsComponent.setSystem("http://snomed.info/sct");
        valueSetExpansionContainsComponent.setDisplay("Hyperreactive airway disease");
        SimpleObject response = new FhirTerminologyServicesToBahmniMapper().mapFhirResponseValueSetToSimpleObject(valueSetExpansionContainsComponent);
        assertNotNull(response);
        assertEquals("Hyperreactive airway disease", response.get("conceptName"));
        assertEquals("195967001", response.get("conceptUuid"));
        assertEquals("Hyperreactive airway disease", response.get("matchedName"));
    }
}