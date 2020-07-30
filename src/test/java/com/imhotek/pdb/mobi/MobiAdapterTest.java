package com.imhotek.pdb.mobi;

import com.imhotek.pdb.PDBReader;
import com.imhotek.pdb.PDBReaderTest;
import com.imhotek.pdb.PalmDataBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MobiAdapterTest {

    private final File mobiFile = new File(PDBReaderTest.MOBI);
    private PalmDataBase palmDataBase;

    private MobiAdapter mobiAdapter;

    @BeforeEach
    void setUp() throws IOException {
        PDBReader pdbReader = new PDBReader();
        palmDataBase = pdbReader.read(mobiFile);
        mobiAdapter = new MobiAdapter(palmDataBase);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getHeaderRecord() {
        MobiHeaderRecord headerRecord = mobiAdapter.getHeaderRecord();
        assertNotNull(headerRecord);
        assertThat(headerRecord.getFullName(), is("The Shakespeare Myth"));
    }

    @Test
    void getBookInfo() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MobiBookInfo mobiBookInfo = mobiAdapter.getBookInfo();
        assertNotNull(mobiBookInfo);
        assertThat(mobiBookInfo.getAuthor(), is("Sir Edwin Durning-Lawrence"));
    }

    @Test
    void getTextContents() {
    }

    @Test
    void appendTextContents() {
    }

    @Test
    void getImage() {
    }

    @Test
    void initHeaderRecord() {
        assertNotNull(mobiAdapter);
    }

    @Test
    void getExtraDataSize() {
    }

    @Test
    void readTextRecord() {
    }
}