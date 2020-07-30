package com.imhotek.pdb.enums;

import static com.imhotek.pdb.Constants.*;

public enum MobiHeaderRecordType {

    drmServerId (DRM_SERVER_ID, STRING),
    drmCommenseId(DRM_COMMENCE_ID, STRING),
    drmEBookBaseId(DRM_EBOOKBASE_BOOK_ID, STRING),
    author(AUTHOR, STRING),
    publisher(PUBLISHER, STRING),
    imprint(IMPRINT, STRING),
    description(DESCRIPTION, STRING),
    isbn(ISBN, STRING),
    subject(SUBJECT, STRING),
    publishingDate(PUBLISH_DATE, STRING),
    review(REVIEW, STRING),
    contributor(CONTRIBUTOR, STRING),
    rights(RIGHTS, STRING),
    subjectCode(SUBJECT_CODE, STRING),
    type(TYPE, STRING),
    source(SOURCE, STRING),
    asin(ASIN, STRING),
    version(VERSION, STRING),
    sample(SAMPLE, STRING),
    startReading(START_READING, INT),
    adult(ADULT, BOOLEAN),
    retailPrice(RETAIL_PRICE, STRING),
    retailCurrency(RETAIL_CURRENCY, STRING),
    coverImageIndex(COVER_OFFSET, INT),
    thumbImageIndex(THUMB_OFFSET, INT),
    hasFakeCover(FAKE_COVER, BOOLEAN),
    creatorSoftware(CREATOR_SOFTWARE, INT),
    creatorMajorVersion(CREATOR_MAJOR_VERS, INT),
    creatorMinorVersion(CREATOR_MINOR_VERS, INT),
    creatorBuildNumber(BUILD_NUMBER, INT),
    lastUpdatetime(LAST_UPDATE, STRING),
    updateTitle(UPDATED_TITLE, STRING),
    fontSignature(FONT_SIGNATURE, STRING),
    language(LANGUAGE, STRING),
    timestamp(TIMESTAME, STRING),
    writingMode(WRITING_MODE, STRING),
    boundaryOffset(KF8_BOUNDARY_OFFSET, INT),
    resourceCount(RESOURCE_COUNT, INT),
    coverUri(KF8_COVER_URI, STRING),
    unknownOne(UNKNOWN, STRING),
    creatorBuildNumberString(CREATOR_BUILD_NUMBER, STRING);

    private final int recordTypeCode;
    private final String fieldType;

    MobiHeaderRecordType(int recordTypeCode, String fieldType) {
        this.recordTypeCode = recordTypeCode;
        this.fieldType = fieldType;

    }

    public int getRecordTypeCode() {
        return this.recordTypeCode;
    }

    public String fieldType() {
        return this.fieldType;
    }

    public static MobiHeaderRecordType fromRecordTypeCode(int recordTypeCode) {
        for(MobiHeaderRecordType type: values()) {
            if(type.recordTypeCode == recordTypeCode) {
                return type;
            }
        }
        return null;
    }
    
}
