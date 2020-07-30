package com.imhotek.pdb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PDBReaderTest {

    public static final String MOBI = "src/test/resources/The Shakespeare Myth by Sir Edwin Durning-Lawrence.mobi";
    static final String EPUB = "src/test/resources/1900 Or the last President.epub";
    private final File mobiFile = new File(MOBI);
    private final File epubFile = new File(EPUB);

    private PDBReader pdbReader;


    @BeforeEach
    void setUp() {
        pdbReader = new PDBReader();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void read_mobi() throws IOException {
        PalmDataBase palmDataBase = pdbReader.read(mobiFile);
        assertThat(palmDataBase.getName(), is("The_Shakespeare_Myth"));
        long expected = 1416724158000L;
        assertThat(palmDataBase.getCreationDate().getTime(), is(expected));
    }

    @Test
    void read_epub() {
        assertThrows(IllegalArgumentException.class, () -> {
            PalmDataBase palmDataBase= pdbReader.read(epubFile);
        });
    }

    @Test
    void readDate() {
    }

    @Test
    void dump() {
    }
}