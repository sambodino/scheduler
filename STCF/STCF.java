import java.io.*;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.HashMap;
/** STCF scheduling framework
 *  @author Sam Knepper
 *  @version 10/21/2016
 */

class Simulator {

  static LinkedList <Job> jobs
      = new LinkedList <>();

    private static PriorityQueue <Job> jobSet
    = new PriorityQueue <Job>(100, new Comparator<Job>(){
        @Override
        public int compare(Job j1, Job j2) {
               if( j1.timeToCompletion != j2.timeToCompletion ) {
                 return j1.timeToCompletion - j2.timeToCompletion ;
               }else{
                 return j1.arrivalTime - j2.arrivalTime ;
               }
             }
     }
    );


  static LinkedList<Integer> run() {

          LinkedList <Integer> turnaroundTimes
          		= new LinkedList <>();

          int jobsCompleted = 0 ;
          int time = 0 ;
          while(jobsCompleted < STCF.totalJobs){

              LinkedList<Job> jobList = STCF.jobMap.get(time);

              if(jobList != null) {
                  for( Job j : jobList ){
                      if( !jobSet.contains(j) )
                          jobSet.add( j ) ;
                  }
              }

              if( !jobSet.isEmpty() ){
                  Job scheduledJob = jobSet.poll();

                  if( scheduledJob.firstTimeScheduled < 0 ){ //first time currentJob has been scheduled
                      scheduledJob.firstTimeScheduled = time ;
                      jobs.add( scheduledJob ) ;
                  }

                  if( scheduledJob.timeToCompletion == 0 ){
                      turnaroundTimes.add(time - scheduledJob.arrivalTime) ;
                      jobsCompleted++ ;
                      if( !jobSet.isEmpty() ){
                          time--; //we want to schedule the next job in the queue right away not a second later.
                      }
                  }else{
                      scheduledJob.timeToCompletion = scheduledJob.timeToCompletion - 1;
                      jobSet.add( scheduledJob ) ;
                  }
              }
              time++;
          }

        return turnaroundTimes ;
     }
}

class Errors {
    static void fatal( String message ) {
      System.err.println( "Fatal error: " + message );
      System.exit( 1 );
    }
   	static void warning( String message ) {
   		System.err.println( "Error: " + message );
   	}

}

class ScanCheck {

  public interface ErrorMessage {
  		abstract String myString();
  }

  static int nextInt( Scanner sc, ErrorMessage message ) {
  		if (sc.hasNextInt()) {
  			return sc.nextInt();
  		} else {
  			Errors.warning(
  				message.myString() +
  				" -- expected an integer"
  			);
  			return 99;
  		}
  	}

}

class Job {

    public int arrivalTime;
    public int timeToCompletion;
    public int firstTimeScheduled = -1 ;

    public Job( int a, Scanner sc ) {

      arrivalTime = a ;

      timeToCompletion = ScanCheck.nextInt(
  			            sc,
  			            () -> Job.this.toString()
      );

    }

}


public class STCF {

    static int totalJobs = 0 ;
    static HashMap <Integer, LinkedList<Job>> jobMap
    		= new HashMap <> ();

    static void startScheduling( Scanner sc ){

      if( sc.hasNext() )
        totalJobs = sc.nextInt(); // first N in file

      while( sc.hasNext() ) {
        int arrival = sc.nextInt();
        LinkedList<Job> jobList = jobMap.get(arrival);
        if(jobList == null) {
           jobList = new LinkedList<Job>();
           jobList.add(new Job(arrival, sc));
           jobMap.put(arrival, jobList);
        }else {
          jobList.add(new Job(arrival, sc));
        }
      }
    }

    public static void main(String [] args) {

        double averageTurnaroundTime = 0 ;
        double averageResponseTime = 0 ;

        if (args.length < 1) {
        			Errors.fatal( "missing file name" );
        }
    		if (args.length > 1) {
    			Errors.fatal( "too many arguments" );
        }
        try {
            startScheduling( new Scanner(new File(args[0])) );
        }
        catch(FileNotFoundException ex) {
            Errors.fatal( "file not found: " + args[0] );
        }

        LinkedList<Integer> turnaroundTimes = Simulator.run();

        for( int tTime : turnaroundTimes ){
            averageTurnaroundTime += (double)tTime ;
        }
        averageTurnaroundTime = averageTurnaroundTime / totalJobs ;
        System.out.println( String.format( "%.5f", averageTurnaroundTime) ) ;

        for( Job j : Simulator.jobs ){
          averageResponseTime += (double)(j.firstTimeScheduled - j.arrivalTime) ;
        }
        averageResponseTime = averageResponseTime / (double)totalJobs ;
        System.out.println( String.format( "%.5f", averageResponseTime) ) ;
    }

}
