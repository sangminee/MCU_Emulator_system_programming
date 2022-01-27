import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader { // OS�� �Ϻ�
	
	// association
	// private FileManager fileManager;  => �������� �ʱ� ������ 
	private Memory memory; 
	private CPU cpu;   

	private Process currentProcess;

	public void loadProcess(String fileName, Memory memory, CPU cpu) {
		this.memory = memory;
		this.cpu = cpu;
		this.currentProcess = new Process();  	
		this.currentProcess.load(fileName,memory,cpu);		

	}

	// CPU���忡���� n���� ���α׷��� ����ǰ� �ְ�, ���μ����� ���������� ���̵� ������ �޸𸮸� ����
	// process = header, code segment, data segment => ������ �̰��� �޸𸮿� �ö��� �ִ�. 
	class Process {
		// �򰥸��� �ʰ� header�� �̸� ������ ��Ŵ	
		// byte ������
		static final short sizeHeader = 4;  // DS = 2����Ʈ, CS=2����Ʈ �� ��� (2byt �� �޸� 1���� ����)
		static final short indexPC = 0 ; 
		static final short indexSP = 1;  

		private short startAddress;
		private short sizeData, sizeCode; 

		private void loadHeader(Scanner scanner) {
			this.sizeData = scanner.nextShort(16);  // 16�������� �о��	
			this.sizeCode = scanner.nextShort(16);
	
			System.out.println("header size: "+ sizeHeader/2);	
			System.out.println("data segment size: " +this.sizeData/2);
			System.out.println("code Segment size : " +this.sizeCode/2);
			System.out.println("----------------------------");
			
			// ����Ʈ�� ���� 2�� ������ �Ѵ�. ������ ��� �ּҸ� 2����Ʈ ������ ���� => �޸� �ּҸ� �� �� 2���� ���		
      		this.startAddress = memory.allocate((short) (sizeHeader/2 + this.sizeData/2 + this.sizeCode/2));  // �����ּ� return			
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
			
			// data segment  -> �̹� memory allocate�� �� �޸𸮰� ����� Ȯ���� �� ���� 
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
