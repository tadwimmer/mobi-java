package com.imhotek.pdb.mobi;

import com.imhotek.pdb.PalmDataBase;
import com.imhotek.pdb.mobi.MobiHeaderRecord.Compression;
import com.imhotek.pdb.mobi.MobiHeaderRecord.Encryption;
import com.imhotek.pdb.mobi.MobiHeaderRecord.MobiType;
import com.imhotek.pdb.mobi.MobiHeaderRecord.TextEncoding;
import hu.webhejj.commons.io.SeekableByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MobiAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(MobiAdapter.class);

	private final PalmDataBase pdb;
	private MobiHeaderRecord headerRecord;
	
	public MobiAdapter(PalmDataBase pdb) {
		this.pdb = pdb;
		initHeaderRecord(pdb);
	}

	public MobiHeaderRecord getHeaderRecord() {
		return headerRecord;
	}
	
	public MobiBookInfo getBookInfo() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return new MobiBookInfo(headerRecord);
	}
	
	public String getTextContents() {
		StringBuilder textBuilder = new StringBuilder(headerRecord.getTextLength());
		try {
			appendTextContents(textBuilder);
		} catch (IOException e) {
			// should never happen for StringBuilder
			throw new RuntimeException(e);
		}
		return textBuilder.toString();
	}
	
	public void appendTextContents(Appendable appendable) throws IOException {
		for(int i = 1; i <= headerRecord.getRecordCount(); i++) {
			String text = readTextRecord(
					pdb.getPalmRecords().get(i).getData(),
					headerRecord.getCompresson(),
					headerRecord.getTextEncoding().toCharset(),
					headerRecord.getExtraDataFlags());
			appendable.append(text);
		}
	}
	
	public byte[] getImage(int index) {
		return pdb.getPalmRecords().get(headerRecord.getFirstImageIndex() + index).getData();
	}
	
	protected void initHeaderRecord(PalmDataBase pdb) {
		
		if(!"BOOK".equals(pdb.getType()) || !"MOBI".equals(pdb.getCreator())) {
			throw new IllegalArgumentException("Don't know how to adapt " + pdb.getType() + pdb.getCreator());
		}
		
		try {
			
			headerRecord = new MobiHeaderRecord();
			
			SeekableByteArrayInputStream sis = new SeekableByteArrayInputStream(pdb.getPalmRecords().get(0).getData());
			DataInputStream dis = new DataInputStream(sis);
			
			headerRecord.setCompresson(Compression.getEnum(dis.readUnsignedShort()));
			
			// ignored, always 0
			dis.readUnsignedShort();
			headerRecord.setTextLength(dis.readInt());
			headerRecord.setRecordCount(dis.readUnsignedShort());
			headerRecord.setRecordSize(dis.readUnsignedShort());
			
			headerRecord.setEncryption(Encryption.getEnum(dis.readUnsignedShort()));
			
			// ignored, always 0
			dis.skipBytes(2);
			
			String magic = ByteIO.readFixedLengthString(dis, 4, StandardCharsets.US_ASCII);
			if(!"MOBI".equals(magic)) {
				throw new IllegalArgumentException("Wrong magic in record: " + magic);
			}
			
			int headerLength = dis.readInt();
			headerRecord.setMobiType(MobiType.getEnum(dis.readInt()));
			headerRecord.setTextEncoding(TextEncoding.getEnum(dis.readInt()));
			headerRecord.setId(dis.readInt());
			headerRecord.setVersion(dis.readInt());
			
			Charset charset = headerRecord.getTextEncoding().toCharset();
			
			// ignored bytes
			dis.skipBytes(40);
			
			headerRecord.setFirstNonBookIndex(dis.readInt());
			int fullNameOffset = dis.readInt();
			int fullNameLength = dis.readInt();
			headerRecord.setLocale(dis.readInt());
			headerRecord.setInputLanguage(dis.readInt());
			headerRecord.setOutputLanguage(dis.readInt());
			headerRecord.setMinVersion(dis.readInt());
			headerRecord.setFirstImageIndex(dis.readInt());
			int huffmanRecordOffset = dis.readInt();
			int huffmanRecordCount = dis.readInt();
			dis.skipBytes(8); // ignored 8 bytes
			int exthFlags = dis.readInt();
			dis.skipBytes(32);
			int drmOffset = dis.readInt();
			int drmCount = dis.readInt();
			int drmSize = dis.readInt();
			int drmFlags = dis.readInt();
			
			if(headerLength == 228 || headerLength == 232) {
				sis.seek(242);
				headerRecord.setExtraDataFlags(dis.readUnsignedShort());
			}
			
			// we read 180 bytes so far, skip to end of MOBI header
			// we should now be at position 16 + headerLength
			sis.seek(16 + headerLength);
			
			// ext header?
			if((exthFlags & 0x40) == 0x40) {
				String exthMagic = ByteIO.readFixedLengthString(dis, 4, charset);
				if(!"EXTH".equals(exthMagic)) {
					throw new IllegalArgumentException("Wrong exth magic in record: " + exthMagic);
				}
	
				/* int extHeaderLength = */ dis.readInt();
				int extRecordCount = dis.readInt();
				for(int i = 0; i < extRecordCount; i++) {
					int recordType = dis.readInt();
					int recordLength = dis.readInt();
					byte[] value = new byte[recordLength - 8];
					dis.readFully(value, 0, recordLength - 8);
					headerRecord.getExtHeaders().add(new ExtHeader(recordType, value));
					// System.out.format("Found ext record %d with with length %d\n", recordType, recordLength);
				}
			}
	
			// read title
			
			sis.seek(fullNameOffset);
			headerRecord.setFullName(ByteIO.readFixedLengthString(dis, fullNameLength, charset));
	
			dis.close();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected int getExtraDataSize(byte[] data, int tail) {
		
		// calculate size of backward-encoded variable-width integer
		int size = 0;
		int pos = 0;
		while(true) {
			int b = data[tail - 1];
			size |= (b & 0x7F) << pos;
			pos += 7;
			tail--;
			if(tail <= 1 || pos > 27 || (b & 0x80) > 0) {
				break;
			}
		}
		
		return size;
	}
	
	protected String readTextRecord(byte[] data, Compression compression, Charset charset, int extraDataFlags) {
		
		int trailingDataLength = 0;
		int otherFlags = extraDataFlags >> 1;
		while(otherFlags > 0) {
			if((otherFlags & 1) > 0) {
				trailingDataLength += getExtraDataSize(data, data.length - trailingDataLength);
			}
			otherFlags >>= 1;
		}
		if((extraDataFlags & 1) > 0) {
			trailingDataLength += (data[data.length - trailingDataLength - 1] & 3) + 1;
		}
		
		switch (compression) {
		case NONE: return new String(data, 0, data.length - trailingDataLength, charset);
		case HUFF_CDIC: throw new IllegalArgumentException("Don't know how to decode Huffman CDIC yet");
		case PALMDOC: return new String(LZ77.decompressLZ77Buffer(data, 0, data.length - trailingDataLength), charset);
		}
		
		throw new IllegalArgumentException("Could not decode record");
	}
}
