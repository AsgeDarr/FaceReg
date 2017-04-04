import java.io.*;


public class Logger {
	private int currentLogging = 0; 
	private String path = "/Users/asgedarr/Dropbox/Dtu/8_semester/Bachelor/workspace/FaceReg/logs/";

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
		while (currentLogging > 0) {try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		currentLogging++;
		writeLog(0,Message);
		
		currentLogging--;
		if(currentLogging == 0){
			notifyAll();
		}
	}

	public synchronized void Error(String Message) throws IOException{
		while (currentLogging > 0) {try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		currentLogging++;
		writeLog(1,Message);
		writeLog(0,Message);
		currentLogging--;
		if(currentLogging == 0){
			notifyAll();
		}
	}

	public void writeLog(int LogLevel, String Message) throws IOException{
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



	public void Trace(){

	}

}
