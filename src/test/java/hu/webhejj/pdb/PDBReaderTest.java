package hu.webhejj.pdb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PDBReaderTest {

    private String mobiFile = "C:\\Users\\twimmer\\Google Drive\\Books\\History & Political Science\\Commentaries on the Laws of England, Book the First by Sir William Blackstone.mobi";
    private File file = new File(mobiFile);

    private PDBReader pdbReader;


    @BeforeEach
    void setUp() {
        pdbReader = new PDBReader();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void read() throws IOException {
        PalmDataBase palmDataBase = pdbReader.read(file);
        assertThat(palmDataBase.getName(), is("Commentaries-Book_the_First"));
    }

    @Test
    void readDate() {
    }

    @Test
    void dump() {
    }
}