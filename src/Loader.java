import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader { // OS의 일부
	
	// association
	// private FileManager fileManager;  => 존재하지 않기 때문에 
	private Memory memory; 
	private CPU cpu;   

	private Process currentProcess;

	public void loadProcess(String fileName, Memory memory, CPU cpu) {
		this.memory = memory;
		this.cpu = cpu;
		this.currentProcess = new Process();  	
		this.currentProcess.load(fileName,memory,cpu);		

	}

	// CPU입장에서는 n개의 프로그램이 실행되고 있고, 프로세스가 개별적으로 아이디를 가지고 메모리를 점유
	// process = header, code segment, data segment => 실제론 이것이 메모리에 올라가져 있다. 
	class Process {
		// 헷갈리지 않게 header에 미리 고정을 시킴	
		// byte 사이즈
		static final short sizeHeader = 4;  // DS = 2바이트, CS=2바이트 씩 사용 (2byt 당 메모리 1번지 제공)
		static final short indexPC = 0 ; 
		static final short indexSP = 1;  

		private short startAddress;
		private short sizeData, sizeCode; 

		private void loadHeader(Scanner scanner) {
			this.sizeData = scanner.nextShort(16);  // 16진법으로 읽어옴	
			this.sizeCode = scanner.nextShort(16);
	
			System.out.println("header size: "+ sizeHeader/2);	
			System.out.println("data segment size: " +this.sizeData/2);
			System.out.println("code Segment size : " +this.sizeCode/2);
			System.out.println("----------------------------");
			
			// 바이트로 쓰면 2로 나눠야 한다. 하지만 모든 주소를 2바이트 단위로 실행 => 메모리 주소를 할 때 2개씩 사용		
      		this.startAddress = memory.allocate((short) (sizeHeader/2 + this.sizeData/2 + this.sizeCode/2));  // 시작주소 return			
      		cpu.setPC((short) (startAddress + sizeHeader/2));                                  
			cpu.setSP((short) (startAddress + sizeHeader/2+ this.sizeCode/2));    

		}
		
		private void loadBody(Scanner scanner) {
			// code segment
			short currentAddress = (short) (this.startAddress + sizeHeader/2); 	
			// DMA
			while (scanner.hasNext()) {  
				memory.store(currentAddress, scanner.nextShort(16));   
				currentAddress++;  		
			}			
			
			// data segment  -> 이미 memory allocate할 때 메모리가 충분히 확보를 해 놓음 
		} 

		public void load(String fileName, Memory memory, CPU cpu) {			
			try {				
				Scanner scanner = new Scanner(new File("exe/" + fileName));
				this.loadHeader(scanner);
				this.loadBody(scanner);				
				scanner.close();				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}

}
