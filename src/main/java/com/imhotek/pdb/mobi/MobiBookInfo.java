package com.imhotek.pdb.mobi;


import com.imhotek.pdb.enums.MobiHeaderRecordType;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

@Setter
@Getter
public class MobiBookInfo {

    private static final Logger LOG = LoggerFactory.getLogger(MobiBookInfo.class);

    private String drmServerId;
    private String drmCommerceId;
    private String drmEbookBaseBookId;
    private String author;
    private String publisher;
    private String imprint;
    private String description;
    private String isbn;
    private String subject;
    private String publishingDate;
    private String review;
    private String contributor;
    private String rights;
    private String subjectCode;
    private String type;
    private String source;
    private String asin;
    private String versionNumber;
    private String sample;
    private int startReading;
    private boolean isAdult;
    private String retailPrice;
    private String retailPriceCurrency;
    private int coverImageIndex = -1;
    private int thumbImageIndex = -1;
    private boolean hasFakeCover;
    private int creatorSoftware;
    private int creatorMajorVersion;
    private int creatorMinorVersion;
    private int creatorBuildNumber;
    private String watermark;
    private String tamperProofKeys;
    private String fontSignature;
    private int clippingLimit;
    private int publisherLimit;
    private boolean textToSpeechEnabled;
    private String cdeType;
    private String lastUpdatetime;
    private String updatedTitle;
    /* new 2020 */
    private String language;
    private String timestamp;
    private String writingMode;
    private int boundaryOffset;
    private String unknownOne;
    private int resourceCount;
    private String coverUri;
    private String creatorBuildNumberString;


    public MobiBookInfo(MobiHeaderRecord mobiHeaderRecord)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {


        Charset charset = mobiHeaderRecord.getTextEncoding().toCharset();
        for (ExtHeader extHeader : mobiHeaderRecord.getExtHeaders()) {
            MobiHeaderRecordType recordType = MobiHeaderRecordType.fromRecordTypeCode(extHeader.getType());
            if (recordType == null) {
                LOG.debug("No MobiHeaderRecordType found for record type code {}", extHeader.getType());
                continue;
            }
            String setterName = "set" + recordType.name().substring(0, 1).toUpperCase() + recordType.name().substring(1);
            Method setter;
            String parameterType = recordType.fieldType();
            switch (parameterType) {
                case "String":
                    setter = this.getClass().getMethod(setterName, String.class);
                    setter.invoke(this, new String(extHeader.getValue(), charset));
                    break;
                case "int":
                    setter = this.getClass().getMethod(setterName, int.class);
                    setter.invoke(this, ByteIO.readInt(extHeader.getValue(), 0));
                    break;
                case "boolean":
                    setter = this.getClass().getMethod(setterName, boolean.class);
                    setter.invoke(this, "yes".equals(new String(extHeader.getValue(), charset)));
                    break;
                default:
                    LOG.debug("Parameter type {} from MobiHeaderRecordType not suppoerted", parameterType);
                    throw new IllegalArgumentException("Parameter type not supported");
            }
        }
    }
}