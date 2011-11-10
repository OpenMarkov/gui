package org.openmarkov.core.gui.configuration;

public enum OperatingSystem {

	WINDOWS(0, "Windows"),
	LINUX(1, "Linux"),
    OTHER(2, "Other");

	private int value;
	
	private String name;

	OperatingSystem(int value, String name) {
		this.value = value;
		this.name = name;
	}

    public int value() { 
    	return value; 
    }
    
    public String toString() {
    	return name;
    }
	
}
