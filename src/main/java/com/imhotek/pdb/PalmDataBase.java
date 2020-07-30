package com.imhotek.pdb;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter @Getter
public class PalmDataBase {

	private String name;
	private int attributes;
	private int version;
	private Date creationDate;
	private Date modificationDate;
	private Date lastBackupDate;
	private long modificationNumber;
	private String type;
	private String creator;
	private long uniqueIdSeed;

	private List<PalmRecord> palmRecords = new ArrayList<>();
	
}
