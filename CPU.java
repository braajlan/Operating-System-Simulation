public class CPU {
	private Queue<PCB> readyQueue;
	private Queue<PCB> waitingQueue;
	private Ram ram;
	private double totalCpuTime = 0;
	private double totalIoTime = 0;
	 static int Interrupt = 0;
	  int counterAB = 0;
	  int counterN = 0;
	public  int availableHDD = 2000000; //KB
	public  int totalHDD = 0;
	int counterHDD = 0;
	int counterNN;
	Queue<PCB> newQueue = new Queue<PCB>();
	//This method is used to "execute" the processes in the Ready Queue
	public void runCpu() {

		ram = new Ram();
		readyQueue = ram.loadToReadyQueue();
		waitingQueue = ram.getWaitingQueue();
		//int l = 1;
		int m = 0; //Number of waiting processes in waiting queue
		int n6 = 0;
		int n16 = 0;

		while (true) {
			m = waitingQueue.length();
			//each 100 Cycle reactivate the Long term scheduler
			if (Timer.time % 100 == 0 || readyQueue.length() == 0) {
				// you should reactivate job scheduler
				readyQueue = ram.loadToReadyQueue(); // new processes from the
				// Job queue
			}

			if (readyQueue.length() == 0 && m != 0) {
				Timer.time++;
				continue;
			}

			if (readyQueue.length() == 0 && m == 0) {
				return;
			}
			PCB p1;
			PCB min = null;
			if(readyQueue.length()==0){
				System.out.println("last");
				break;
			}
			if(readyQueue.length() == 1) {
				p1 = readyQueue.serve(); //First process to be executed
				//newQueue.enqueue(p1);
			} else {

				int n1=0; //
				int n = readyQueue.length(); 
				min = readyQueue.serve(); //proccess 1

				for(int i=0;i<n;i++) {
					PCB p = readyQueue.serve();	//process 2
					n1 = min.getCycles().peek().getCpuBurst();
					int n2 = p.getCycles().peek().getCpuBurst();
					if(n1 > n2) {
						readyQueue.enqueue(min);
						min = p;
					} else {
						readyQueue.enqueue(p);
					}
				}
				p1 = min;
			}

			p1.setStatus("Running");
			p1.CPUNumIncrement();
			Cycle c = p1.getFirstCycle();

			totalCpuTime += c.getCpuBurst();
			p1.increaseCPUSum(c.getCpuBurst());
			//	System.out.println(c.getCpuBurst());
			// waiting for io
			for (int j = 0; j < c.getIOBurst(); j++) {
				Timer.time++;
				p1.setStatus("Waiting");
				//ram.addToReadyQueue(waitingQueue.serve());
			}
			
			if (c.getIOBurst() > 0) {
				p1.IONumIncrement();
			}
			totalIoTime += c.getIOBurst();
			p1.increaseIOSum(c.getIOBurst());
			p1.increaseMemorySum(c.getMemory());

			// if (c.getIOBurst() == 0 && c.getMemory() == 0) { //This is the last cycle for this process
			if(p1.getCycles().length() == 1){
			//	c = p1.getFirstCycle();

				if(totalHDD < 2000000) {
					p1.setStatus("Terminated");
					totalHDD+= p1.getMemorySum();
					p1.setEndTime(Timer.time);
					p1.increaseMemorySum(c.getMemory());
					newQueue.enqueue(p1);
					ram.addToFinshedQueue(p1);
					counterHDD++;
				} else {
					System.out.println("full");
					break;
				}
			}
			if (p1.getPid() == 6) {
				//Special Case with process 6
				n6++;
				if (n6 == 3) {
					if(totalHDD < 2000000) {
						totalHDD+=p1.getMemorySum();
						p1.setStatus("Terminated");
						p1.setEndTime(Timer.time);
						p1.increaseMemorySum(c.getMemory());
				//			newQueue.enqueue(p1);
						ram.addToFinshedQueue(p1);
						//counterHDD++;
					} else {
						System.out.println("full");
						break;
					}
					//				}
				}
			}
			if (p1.getPid() == 16 ) { //Special Case with process 16
				n16++;
				if (n16 == 3) {
					if(totalHDD < 2000000) {
						p1.setStatus("Terminated");
						totalHDD+=p1.getMemorySum();
						p1.setEndTime(Timer.time);
						p1.increaseMemorySum(c.getMemory());
//newQueue.enqueue(p1);
						ram.addToFinshedQueue(p1);

					} else {
						System.out.println("full");
						break;
					}
				}
			}
			//			p1.getCycles().serve();
			ram.addToReadyQueue(p1); //After executing the cycle , return process to ready queue
			//end of loop		
			
		}
	}

	//	public int computeHDDsize(Ram r) {
	//		int total = 0;
	//		Queue<PCB> temp = r.getFinshedProcesses();
	//		System.out.println(temp.length());
	//		for(int i=0;i<temp.length();i++){
	//			PCB p = temp.serve();
	//			total = p.getMemorySum();	
	//			System.out.println(total +"jj");
	//		}
	//		return total;
	//	}
	//	
	
	public double computeAVG(){
		return (totalHDD/counterHDD);
	}
	public double computeNormally(){
		return (counterN/counterHDD);
	}
	public void counter() {
		int n = newQueue.length();
		for(int i=0;i<n;i++) {
			PCB temp = newQueue.serve(); 
	
			if(!IOdevice(temp)&&!IORequest(temp)&&!GenerateInterrupt(temp)&&!terminatesAN(temp) && !terminatesN(temp)){counterNN++;}
		IOdevice(temp);
				IORequest(temp);
					GenerateInterrupt(temp);
							terminatesN(temp);
							
							if(terminatesAN(temp)){
								counterAB++;
			} else
				                counterN++;
			}
		}
	

	public boolean terminatesN(PCB p1){
		double total = p1.getIOSum()*0.05;
		if(total>0 && total <0.9999999999) { //if interrupt occured 
			Interrupt++;
			return true;
		}
		return false;
	}
	//========================================================= Process is Terminate Abnormally                                                                
	public boolean terminatesAN(PCB p1){
		double total = p1.getIOSum()*0.01;
		if(total>0 && total <0.9999999999) {
			Interrupt++;
			return true;
		}
		return false;
	}

	public boolean GenerateInterrupt(PCB p1){
		double total = p1.getIOSum()*0.10;
		if(total>0 && total <0.9999999999) {
			Interrupt++;
			return true;
		}
		return false;
	}
	//========================================================= To Generate an I/O Request                                                               

	public boolean IORequest(PCB p1){
		double total = p1.getIOSum()*0.20;
		if(total>0 && total <0.9999999999) {
			Interrupt++;
			return true;
		}
		return false;
	}
	
	public boolean IOdevice(PCB p1){
		double total = p1.getIOSum()*0.20;
		if(total>0 && total <0.9999999999) {
			Interrupt++;
			return true;
		}
		return false;
	}
	//========================================================= Process is Terminate Normally                                                                

	//========================================================= Simulation is Terminate                                                                

	public Queue<PCB> getReadyQueue() {
		return readyQueue;
	}

	public void setReadyQueue(Queue<PCB> readyQueue) {
		this.readyQueue = readyQueue;
	}

	public Ram getRam() {
		return ram;
	}

	public void setRam(Ram ram) {
		this.ram = ram;
	}

	public double getTotalCpuTime() {
		return totalCpuTime;
	}

	public void setTotalCpuTime(int totalCpuTime) {
		this.totalCpuTime = totalCpuTime;
	}

	public double getTotalIoTime() {
		return totalIoTime;
	}

	public void setTotalIoTime(double totalIoTime) {
		this.totalIoTime = totalIoTime;
	}

}

