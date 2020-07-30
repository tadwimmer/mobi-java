package com.imhotek.pdb;

import com.imhotek.pdb.mobi.ByteIO;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class PDBReader {
	
	protected static final long DATE_OFFSET = 0L;

	public PalmDataBase read(File file) throws IOException {
		
		Charset charset = StandardCharsets.US_ASCII;
		
        if(!file.getName().endsWith(".mobi")) {
        	throw new IllegalArgumentException("PalmDataBaseReader can only read .mobi files");
		}
		try(DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file))) {

			PalmDataBase palmDataBase = new PalmDataBase();
			palmDataBase.setName(ByteIO.readNullTerminatedString(dataInputStream, charset));
			for(int i = 31; i > palmDataBase.getName().length(); i--) {
				dataInputStream.readByte();
			}
			
			palmDataBase.setAttributes(dataInputStream.readUnsignedShort());
			palmDataBase.setVersion(dataInputStream.readUnsignedShort());
			palmDataBase.setCreationDate(readDate(dataInputStream));
			palmDataBase.setModificationDate(readDate(dataInputStream));
			palmDataBase.setLastBackupDate(readDate(dataInputStream));
			palmDataBase.setModificationNumber(dataInputStream.readInt());
			int appInfoOffset = dataInputStream.readInt();
			int sortInfoOffset = dataInputStream.readInt();
			palmDataBase.setType(ByteIO.readFixedLengthString(dataInputStream, 4, charset));
			palmDataBase.setCreator(ByteIO.readFixedLengthString(dataInputStream, 4, charset));
			palmDataBase.setUniqueIdSeed(dataInputStream.readLong());

			if(appInfoOffset != 0) {
				throw new IllegalArgumentException("Can't handle app info yet, found offset " + appInfoOffset);
			}
			if(sortInfoOffset != 0) {
				throw new IllegalArgumentException("Can't handle app info yet, found offset " + sortInfoOffset);
			}

			int entryCount = dataInputStream.readUnsignedShort();
			int[] offsets = addPalmRecords(dataInputStream, palmDataBase, entryCount);

			// skip to first record
			dataInputStream.skipBytes(offsets[0] - 78 - (8 * entryCount));

			setDataIntoPalmRecords(dataInputStream, palmDataBase, offsets);
			return palmDataBase;
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
				ByteArrayOutputStream stream = new ByteArrayOutputStream(dis.available());
				int count;
				while((count = dis.read(buf)) >= 0) {
					stream.write(buf, 0, count);
				}
				palmRecord.setData(stream.toByteArray());
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
		return new Date(time * 1000 + DATE_OFFSET);
	}
	


}
