import java.io.*;
import java.util.TreeMap;


public class Logger { 
	private String path = "/Users/asgedarr/Dropbox/Dtu/8_semester/Bachelor/workspace/FaceReg/logs/";

	// Used for search
	private TreeMap<Integer,LogValues> logMap = new TreeMap<Integer, LogValues>(); 
	private Boolean binarySearchFail = false;

	private volatile int currentLogging = 0;
	public void setNum(int x) {
	    this.currentLogging = x; 
	  }

	  public int getNum() {
	    return currentLogging;
	  }
	

	
	public Logger(){
		File LogDebug = new File(path + "logDebug.txt");
		try {
			if(!LogDebug.isFile() && !LogDebug.createNewFile()){
				throw new IOException("Creating new logfile : " + LogDebug.getAbsolutePath());
			}
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		File LogError = new File(path + "logError.txt");
		try {
			if(!LogError.isFile() && !LogError.createNewFile()){
				throw new IOException("Creating new logfile : " + LogError.getAbsolutePath());
			}
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}


	public synchronized void Debug(String Message) throws IOException{
//		System.out.println("1- Debug - currentLogging : " + currentLogging );
		while (currentLogging > 0) {try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		currentLogging++;
//		System.out.println("2 - Debug - currentLogging : " + currentLogging );
		writeLog(0,Message);
		
		currentLogging--;
//		System.out.println("3 - Debug - currentLogging : " + currentLogging );
		if(currentLogging == 0){
			notifyAll();
		}
	}

	public synchronized void Error(String Message) throws IOException{
//		System.out.println("1 - Error currentLogging : " + currentLogging );
		while (currentLogging > 0) {try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		
		currentLogging++;
//		System.out.println("2- Error currentLogging : " + currentLogging );
		writeLog(1,Message);
		writeLog(0,Message);
		currentLogging--;
//		System.out.println("3- Error currentLogging : " + currentLogging );
		if(currentLogging == 0){
			notifyAll();
		}
	}

	public synchronized void writeLog(int LogLevel, String Message) throws IOException{
		if (LogLevel < 1 ){
			try(FileWriter fw = new FileWriter(path + "logDebug.txt", true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw))
			{
				out.println(java.time.LocalDateTime.now() + ";" + Message);

			} catch (IOException e) {

			}	
		}else{
			try(FileWriter fw = new FileWriter(path + "logError.txt", true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw))
			{
				out.println(java.time.LocalDateTime.now() + ";" + Message);

			} catch (IOException e) {

			}
		}
	}


	
	
	
	// ------------------ Search Algorithms ------------------

	public synchronized void setLogSearch(String searchValue) throws IOException{
//		System.out.println("1 - Search currentLogging : " + currentLogging );
		while (currentLogging > 0) {try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		currentLogging++;
//		System.out.println("2 - Search currentLogging : " + currentLogging );
		binarySearchFail = false;
		if(searchValue.isEmpty()){
			System.out.println("Type a search value. Type unknown to find unknown faces.");
		}
		searchValue = searchValue.toLowerCase();
		System.out.println("Searching for: " + searchValue);
		BufferedReader br = new BufferedReader(new FileReader(path + "logDebug.txt"));
		String line =  null;
		int i = 0, matchIndex;
		while((line=br.readLine())!=null){
			String str[] = line.split(";");
			int firstDigitIndex = 0;
			while (firstDigitIndex < str[1].length() && !Character.isDigit(str[1].charAt(firstDigitIndex))) firstDigitIndex++;
			logMap.put(i, new LogValues(str[0],str[1].substring(0, firstDigitIndex),Double.parseDouble(str[3])));
			i++;
		}
//		System.out.println(logMap.toString());
		quickSort(0, logMap.size()-1);
//		System.out.println(logMap.toString());
		matchIndex = binarySearch(searchValue, 0, logMap.size()-1);



		if(binarySearchFail){
			System.out.println("No match to be found.");
		}else{
//			System.out.println("\n"+"Match at index: "+ matchIndex + "\n");
			writeFoundLogs(matchIndex,searchValue);
		}
		currentLogging--;
//		System.out.println("3 - Search currentLogging : " + currentLogging );
	}


	public void quickSort(int p, int r){
		int i=p, j=r;
		int pivot = (int)(Math.random() * (logMap.size()-1)); 
		while(i <= j){
			while(logMap.get(i).getMatch().compareTo(logMap.get(pivot).getMatch())< 0){
				i++;
			}
			while(logMap.get(j).getMatch().compareTo(logMap.get(pivot).getMatch())>0 ){
				j--;
			}
			if (i <= j) {
				swap(i, j);
				i++;
				j--;
			}
		}
		if (p < j)
			quickSort(p, j);
		if (i < r)
			quickSort(i, r);
	}


	public void swap(int i,int j){	
		TreeMap<Integer,LogValues> tempMap = new TreeMap<Integer, LogValues>(); 
		tempMap.put(0, new LogValues(this.logMap.get(i).getTime(),this.logMap.get(i).getMatch(),
				this.logMap.get(i).getDistance()));
		this.logMap.put(i,new LogValues(this.logMap.get(j).getTime(),this.logMap.get(j).getMatch(),
				this.logMap.get(j).getDistance()));
		this.logMap.put(j,new LogValues(tempMap.get(0).getTime(),tempMap.get(0).getMatch(),
				tempMap.get(0).getDistance()));

	}

	public int binarySearch(String searchValue, int i, int j){
		if(j<i){
			binarySearchFail = true;
			return 0;
		}
		int m;
		m = (int) Math.floor((i+j)/2);
		if(logMap.get(m).getMatch().compareTo(searchValue)==0){
			return m;
		}else if(logMap.get(m).getMatch().compareTo(searchValue)<0){
			return binarySearch(searchValue,m+1,j);

		}else{
			return binarySearch(searchValue,i,m-1);
		}
	}

	public void writeFoundLogs(int index, String searchValue) throws IOException{
		try (FileWriter writeFoundLogs = new FileWriter(path +"searchValues.txt")){
			int start=0,end=logMap.size()-1;
			for(int i = index; i>=0; i--){
				if(this.logMap.get(i).getMatch().compareTo(searchValue)==0){
					start = i;
				}else{
					break;
				}
			}
			for(int i = index; i<logMap.size(); i++){
				if(this.logMap.get(i).getMatch().compareTo(searchValue)==0){
					end = i;
				}else{
					break;
				}
			}
			//System.out.println("Start: " + start + " End: " + end );
			for(int i= start; i<= end; i++){
				System.out.print(logMap.get(i).toString());
				writeFoundLogs.write(logMap.get(i).toString());
			}

		}
	}
	
	
	
}
