import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class FCFSScheduler{

	 static int numberOfProcesses;//N
	 //processes array will have a 2D array size of [N][4]
	 //[][0] for arrival time, [][1] for execution time, [][2] for first run time
	 //, and [][3] for completion time
	 static int[][] processes;
	 static int timeCount = 0;//keeps track of current time
	 static double averageTurnaroundTime = 0;
	 static double averageResponseTime = 0;
	public static void readFile (String filePath) throws IOException{
		int i = 0;
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;

		numberOfProcesses = Integer.parseInt(br.readLine());
		processes = new int[numberOfProcesses][4];
		while ((line = br.readLine()) != null) {
			if(!line.trim().isEmpty()) {
				processes[i][0] = Integer.parseInt(line);
				line = br.readLine();
				processes[i++][1] = Integer.parseInt(line);
			}
		}
		br.close();
	}

	//stable sorting algorithm from java library.
	public static void sortProcesses (){
		java.util.Arrays.sort(processes, new java.util.Comparator<int[]>() {
	    public int compare(int[] a, int[] b) {
	        return Integer.compare(a[0], b[0]);
	    }
	});
	}

/*		//set the process' first run time
		if (processes[n][2] == -1) processes[n][2] = timeCount;
		timeCount++;
		processes[n][1]--;
		//System.out.println("Job " + n +" has " + processes[n][1] + "left");
		//if current process is finished
		if(processes[n][1] == 0){
			//set the process' completion time
			processes[n][3] = timeCount;
			//finished process will have execution time of -1
			processes[n][1] = -1;
			//update the number of total jobs done
			numOfJobDone++;	}	*/

	//will run a process at processes[n][]
	public static void runAProcess (int n){
		//if currently there is no process to run, warp time
	if(timeCount<processes[n][0]) timeCount=processes[n][0];
		//set the process' first run time
		processes[n][2] = timeCount;
		while(processes[n][1] > 0){
			processes[n][1]--;
			timeCount++;
		}
		//set the process' completion time
		processes[n][3] = timeCount;
	}

	public static void calculateAvgTimes (){
	for (int i = 0; i < numberOfProcesses; i++){
		averageTurnaroundTime += processes[i][3] - processes[i][0];
		averageResponseTime += processes[i][2] - processes[i][0];
	}
	averageTurnaroundTime = (averageTurnaroundTime-processes[0][0])/numberOfProcesses;
	averageResponseTime = averageResponseTime/numberOfProcesses;
	}



	public static void main(String [] args){

		try	{
			readFile(args[0]);
			//readFile(args[0]);//when its running from command line arguments
		}
		catch (IOException ex){
	    System.err.println("Caught IOException: " + ex.getMessage());
		}


		sortProcesses();
	/*	for(int z = 0; z < numberOfProcesses; z++){
			System.out.println(z + "is " + processes[z][0] + ":" + processes[z][1]);
		}*/
		for(int i = 0; i < numberOfProcesses; i++){
			runAProcess(i);
		}
		calculateAvgTimes();
		System.out.printf("%.5f",averageTurnaroundTime);
		System.out.println();
		System.out.printf("%.5f",averageResponseTime);
		System.out.println();
	}

}
	
