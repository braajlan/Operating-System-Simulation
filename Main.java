public class Main {

	public static void main(String args[]) {
		

		Ram r1 = new Ram();

		CPU cpu1 = new CPU();
		cpu1.runCpu();
		Queue<PCB> jobs = cpu1.getRam().getFinshedProcesses();
		System.out.println();
		double CPUProcessing = cpu1.getTotalCpuTime();
		double CPUTotalTime = Timer.time;
		double Utilization = CPUProcessing / CPUTotalTime;
		int i = 0;
		cpu1.counter();

		System.out.println("(1) number of processess stored in HDD/ "+ cpu1.counterHDD);
		System.out.println("(2) avg of processess size of all jobs/" + cpu1.computeAVG());
		System.out.println("(3) normally processes/ " + cpu1.counterN);
		System.out.println("(4) normally processes/ " + 	cpu1.counterAB);
		System.out.println("(5) cpu bound/ " + cpu1.counterNN);
		

//		try {
//			WriteToFile(jobs, CPUProcessing, Utilization,  CPUTotalTime);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		System.out.println("Executeing finished!");

	}
}



