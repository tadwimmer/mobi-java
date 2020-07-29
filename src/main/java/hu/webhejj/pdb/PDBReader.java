package hu.webhejj.pdb;

import hu.webhejj.pdb.mobi.ByteIO;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class PDBReader {
	
	protected static long DATEOFFSET = -2082844800000L;

	public PalmDataBase read(File file) throws IOException {
		
		Charset charset = StandardCharsets.US_ASCII;
		

		try(DataInputStream dis = new DataInputStream(new FileInputStream(file))) {

			PalmDataBase pdb = new PalmDataBase();
			pdb.setName(ByteIO.readNullTerminatedString(dis, charset));
			for(int i = 31; i > pdb.getName().length(); i--) {
				dis.readByte();
			}
			
			pdb.setAttributes(dis.readUnsignedShort());
			pdb.setVersion(dis.readUnsignedShort());
			pdb.setCreationDate(readDate(dis));
			pdb.setModificationDate(readDate(dis));
			pdb.setLastBackupDate(readDate(dis));
			pdb.setModificationNumber(dis.readInt());
			int appInfoOffset = dis.readInt();
			int sortInfoOffset = dis.readInt();
			pdb.setType(ByteIO.readFixedLengthString(dis, 4, charset));
			pdb.setCreator(ByteIO.readFixedLengthString(dis, 4, charset));
			pdb.setUniqueIdSeed(dis.readLong());
			

			
			if(appInfoOffset != 0) {
				throw new IllegalArgumentException("Can't handle app info yet, found offset " + appInfoOffset);
			}
			if(sortInfoOffset != 0) {
				throw new IllegalArgumentException("Can't handle app info yet, found offset " + sortInfoOffset);
			}

			int entryCount = dis.readUnsignedShort();
			int[] offsets = addPalmRecords(dis, pdb, entryCount);

			// skip to first record
			dis.skipBytes(offsets[0] - 78 - (8 * entryCount));

			setDataIntoPalmRecords(dis, pdb, offsets);
			return pdb;
		}
	}

	private void setDataIntoPalmRecords(DataInputStream dis, PalmDataBase pdb, int[] offsets) throws IOException {
		int i = 0;
		for(PalmRecord palmRecord: pdb.getPalmRecords()) {

			if(i < offsets.length -1) {
				int length = offsets[i + 1] - offsets[i];
				palmRecord.setData(new byte[length]);
				dis.readFully(palmRecord.getData());

			} else {
				byte[] buf = new byte[4096];
				ByteArrayOutputStream baos = new ByteArrayOutputStream(dis.available());
				int count;
				while((count = dis.read(buf)) >= 0) {
					baos.write(buf, 0, count);
				}
				palmRecord.setData(baos.toByteArray());
			}
			i++;
		}
	}

	private int[] addPalmRecords(DataInputStream dis, PalmDataBase pdb, int entryCount) throws IOException {
		int[] offsets = new int[entryCount];
		for(int i = 0; i < entryCount; i++) {
			offsets[i] = dis.readInt();
			PalmRecord palmRecord = new PalmRecord();
			palmRecord.setAttributes(dis.readUnsignedByte());
			palmRecord.setId((dis.readUnsignedByte() << 16) | dis.readUnsignedShort());
			pdb.getPalmRecords().add(palmRecord);

		}
		return offsets;
	}

	protected Date readDate(DataInputStream dis) throws IOException {
		long time = dis.readInt();
		return new Date(time * 1000 + DATEOFFSET);
	}
	


}
