package org.bahmni.module.admin.encounter;

import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.observation.DiagnosisImportService;
import org.bahmni.module.admin.observation.ObservationImportService;
import org.bahmni.module.bahmnicore.util.VisitIdentificationHelper;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class BahmniEncounterTransactionImportService {

    private EncounterService encounterService;
    private final ObservationImportService observationService;
    private final DiagnosisImportService diagnosisService;
    private VisitIdentificationHelper visitIdentificationHelper;

    public BahmniEncounterTransactionImportService(EncounterService encounterService,
                                                   ObservationImportService observationService, DiagnosisImportService diagnosisService, VisitIdentificationHelper visitIdentificationHelper) {
        this.encounterService = encounterService;
        this.observationService = observationService;
        this.diagnosisService = diagnosisService;
        this.visitIdentificationHelper = visitIdentificationHelper;
    }

    public BahmniEncounterTransaction getBahmniEncounterTransaction(EncounterRow encounterRow, Patient patient) throws ParseException {
        EncounterType requestedEncounterType = encounterService.getEncounterType(encounterRow.encounterType);
        Visit matchingVisit = visitIdentificationHelper.getVisitFor(patient, encounterRow.visitType, encounterRow.getEncounterDate());
        Date visitStartDatetime = matchingVisit.getStartDatetime();

        DuplicateObservationsMatcher duplicateObservationsMatcher = new DuplicateObservationsMatcher(matchingVisit, requestedEncounterType);

        List<EncounterTransaction.Observation> bahmniObservations = observationService.getObservations(encounterRow, visitStartDatetime, duplicateObservationsMatcher);
        List<BahmniDiagnosisRequest> bahmniDiagnosis = diagnosisService.getBahmniDiagnosis(encounterRow, visitStartDatetime, duplicateObservationsMatcher);

        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        bahmniEncounterTransaction.setBahmniDiagnoses(bahmniDiagnosis);
        bahmniEncounterTransaction.setObservations(bahmniObservations);
        bahmniEncounterTransaction.setPatientUuid(patient.getUuid());
        bahmniEncounterTransaction.setEncounterDateTime(visitStartDatetime);
        bahmniEncounterTransaction.setEncounterTypeUuid(requestedEncounterType.getUuid());
        bahmniEncounterTransaction.setVisitTypeUuid(matchingVisit.getVisitType().getUuid());
        bahmniEncounterTransaction.setVisitUuid(matchingVisit.getUuid());

        return bahmniEncounterTransaction;
    }

}