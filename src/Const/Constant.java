package Const;

import GenericGet.GenericGet;

public class Constant {

//	public static final String PI_HOME = "http://192.168.1.65";
//	public static final String PI_HOME = "http://192.168.137.214";
	public static final String PI_HOME = "http://192.168.137.18";
	public static final String PORT = ":8080";
	public static final String PATH_PREFIX = "/submarine";
	public static Integer tick_ms = 500;
	public static final int ERROR = -999999;
	public static final String COMMS_LOST = "COMMUNICATION LOST";
	public static final String COMMS_OK = "COMMUNICATION OK";

	public static GenericGet gg = null;
	static {
		gg = new GenericGet();
	}
	
}
