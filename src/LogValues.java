public class LogValues {
	private String time;
	private String match;
	private double distance;
	
	public LogValues(String time, String match, double distance){
		this.time = time;
		this.match = match;
		this.distance = distance;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public String toString(){
		String logValues = ("TimeStamp; " + time + "; Match; " + match + ";Distance; " + distance+";\n");
		return logValues;
	}
	
}
