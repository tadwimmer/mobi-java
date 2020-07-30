package com.imhotek.pdb.mobi;

import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
public class MobiHeaderRecord {

	public static final String UNKNOWN_ENUM_VALUE = "Unknown enum value ";

	public enum Compression {
		NONE(1),
		PALMDOC(2),
		HUFF_CDIC(17480);
		
		private final int value;
		Compression(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		public static Compression getEnum(int intValue) {
			for(Compression type: Compression.values()) {
				if(type.getValue() == intValue) {
					return type;
				}
			}
			throw new IllegalArgumentException(UNKNOWN_ENUM_VALUE + intValue);
		}		
	}
	
	public enum Encryption {
		NONE(0),
		OLD_MOBIPOCKET(1),
		MOBIPOCKET(2);
		
		private final int value;
		Encryption(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		public static Encryption getEnum(int intValue) {
			for(Encryption type: Encryption.values()) {
				if(type.getValue() == intValue) {
					return type;
				}
			}
			throw new IllegalArgumentException(UNKNOWN_ENUM_VALUE + intValue);
		}		
	}
	
	public enum MobiType {
		MOBIPOCKET_BOOK(2),
		PALMDOC_BOOK(3),
		AUDIO(4),
		NEWS(257),
		NEWS_FEED(258),
		NEWS_MAGAZINE(259),
		PICS(513),
		WORD(514),
		XLS(515),
		PPT(516),
		TEXT(517),
		HTML(518);	
		
		private final int value;
		MobiType(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		public static MobiType getEnum(int intValue) {
			for(MobiType type: MobiType.values()) {
				if(type.getValue() == intValue) {
					return type;
				}
			}
			throw new IllegalArgumentException(UNKNOWN_ENUM_VALUE + intValue);
		}
	}
	
	public enum TextEncoding {
		CP1252(1252),
		UTF8(65001);
		
		private final int value;
		TextEncoding(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		public static TextEncoding getEnum(int intValue) {
			for(TextEncoding type: TextEncoding.values()) {
				if(type.getValue() == intValue) {
					return type;
				}
			}
			throw new IllegalArgumentException(UNKNOWN_ENUM_VALUE + intValue);
		}
		public Charset toCharset() {
			switch(this) {
			case CP1252: return Charset.forName("CP1252");
			case UTF8:

			default : return StandardCharsets.UTF_8;
			}
		}
	}	
	
	private Compression compresson;
	private int textLength;
	private int recordCount;
	private int recordSize;
	private Encryption encryption;
	private MobiType mobiType;
	private TextEncoding textEncoding;
	private long id;
	private long version;
	private long minVersion;
	private String fullName;
	
	private int firstNonBookIndex;
	private int firstImageIndex;
	
	private int locale;
	private int inputLanguage;
	private int outputLanguage;
	
	private int extraDataFlags;
	
	private List<ExtHeader> extHeaders = new ArrayList<>();

}
