package org.bahmni.module.referencedata.labconcepts.contract;

public class Sample extends Resource {
    private String shortName;
    public static final String SAMPLE_PARENT_CONCEPT_NAME = "Lab Samples";
    public static final String SAMPLE_CONCEPT_CLASS = "Sample";

    public Sample() {
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

}