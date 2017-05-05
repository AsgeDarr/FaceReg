//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.TreeMap;
//
//
//public class LogSearch {
//	private String path = "/Users/asgedarr/Dropbox/Dtu/8_semester/Bachelor/workspace/FaceReg/logs/";
//	private TreeMap<Integer,LogValues> logMap = new TreeMap<Integer, LogValues>(); 
//	private Boolean binarySearchFail = false;
//
//	public void setLogSearch(String searchValue) throws IOException{
//		binarySearchFail = false;
//		if(searchValue.isEmpty()){
//			System.out.println("Type a search value. Type unknown to find unknown faces.");
//		}
//		searchValue = searchValue.toLowerCase();
//		System.out.println("Searching for: " + searchValue);
//		BufferedReader br = new BufferedReader(new FileReader(path + "logDebug.txt"));
//		String line =  null;
//		int i = 0, matchIndex;
//		while((line=br.readLine())!=null){
//			String str[] = line.split(";");
//			int firstDigitIndex = 0;
//			while (firstDigitIndex < str[1].length() && !Character.isDigit(str[1].charAt(firstDigitIndex))) firstDigitIndex++;
//			logMap.put(i, new LogValues(str[0],str[1].substring(0, firstDigitIndex),Double.parseDouble(str[3])));
//			i++;
//		}
////		System.out.println(logMap.toString());
//		quickSort(0, logMap.size()-1);
////		System.out.println(logMap.toString());
//		matchIndex = binarySearch(searchValue, 0, logMap.size()-1);
//
//
//
//		if(binarySearchFail){
//			System.out.println("No match to be found.");
//		}else{
////			System.out.println("\n"+"Match at index: "+ matchIndex + "\n");
//			writeFoundLogs(matchIndex,searchValue);
//		}
//	}
//
//
//	public void quickSort(int p, int r){
//		int i=p, j=r;
//		int pivot = (int)(Math.random() * (logMap.size()-1)); 
//		while(i <= j){
//			while(logMap.get(i).getMatch().compareTo(logMap.get(pivot).getMatch())< 0){
//				i++;
//			}
//			while(logMap.get(j).getMatch().compareTo(logMap.get(pivot).getMatch())>0 ){
//				j--;
//			}
//			if (i <= j) {
//				swap(i, j);
//				i++;
//				j--;
//			}
//		}
//		if (p < j)
//			quickSort(p, j);
//		if (i < r)
//			quickSort(i, r);
//	}
//
//
//	public void swap(int i,int j){	
//		TreeMap<Integer,LogValues> tempMap = new TreeMap<Integer, LogValues>(); 
//		tempMap.put(0, new LogValues(this.logMap.get(i).getTime(),this.logMap.get(i).getMatch(),
//				this.logMap.get(i).getDistance()));
//		this.logMap.put(i,new LogValues(this.logMap.get(j).getTime(),this.logMap.get(j).getMatch(),
//				this.logMap.get(j).getDistance()));
//		this.logMap.put(j,new LogValues(tempMap.get(0).getTime(),tempMap.get(0).getMatch(),
//				tempMap.get(0).getDistance()));
//
//	}
//
//	public int binarySearch(String searchValue, int i, int j){
//		if(j<i){
//			binarySearchFail = true;
//			return 0;
//		}
//		int m;
//		m = (int) Math.floor((i+j)/2);
//		if(logMap.get(m).getMatch().compareTo(searchValue)==0){
//			return m;
//		}else if(logMap.get(m).getMatch().compareTo(searchValue)<0){
//			return binarySearch(searchValue,m+1,j);
//
//		}else{
//			return binarySearch(searchValue,i,m-1);
//		}
//	}
//
//	public void writeFoundLogs(int index, String searchValue) throws IOException{
//		try (FileWriter writeFoundLogs = new FileWriter(path +"searchValues.txt")){
//			int start=0,end=logMap.size()-1;
//			for(int i = index; i>=0; i--){
//				if(this.logMap.get(i).getMatch().compareTo(searchValue)==0){
//					start = i;
//				}else{
//					break;
//				}
//			}
//			for(int i = index; i<logMap.size(); i++){
//				if(this.logMap.get(i).getMatch().compareTo(searchValue)==0){
//					end = i;
//				}else{
//					break;
//				}
//			}
//			//System.out.println("Start: " + start + " End: " + end );
//			for(int i= start; i<= end; i++){
//				System.out.print(logMap.get(i).toString());
//				writeFoundLogs.write(logMap.get(i).toString());
//			}
//
//		}
//	}
//
//}
//
//
//
//
