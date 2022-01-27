public class Memory {

	private short memory[]; // 빈 memory가 할당되어야 한다.

	// constructor(생성자) - new 연산자와 같이 사용되어 클래스로부터 객체를 생성할 때 호출되어 객체의 초기화를 담당
	public Memory() {
		this.memory =new short[512];	
	}

	public short load(short mar) {	
		return memory[mar];    
	}
	
	public void store(short mar, short mbr) { 
		this.memory[mar] = mbr;
	}	

	public short allocate(short Tsize) {
		// 현재 실행되는 Process의 전체 사이즈를 계산해 두고. PC값 즉 code Segment의 시작주소를 반환
		// 메모리에 프로그램이 하나밖에 load가 안되는 MCU simulator이기 때문에 0이 반환되어야 함
		System.out.println("현재 Process가 사용하는 메모리 size: "+ Tsize);	
		return 0;
   }
	
}
