public class Memory {

	private short memory[]; // �� memory�� �Ҵ�Ǿ�� �Ѵ�.

	// constructor(������) - new �����ڿ� ���� ���Ǿ� Ŭ�����κ��� ��ü�� ������ �� ȣ��Ǿ� ��ü�� �ʱ�ȭ�� ���
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
		// ���� ����Ǵ� Process�� ��ü ����� ����� �ΰ�. PC�� �� code Segment�� �����ּҸ� ��ȯ
		// �޸𸮿� ���α׷��� �ϳ��ۿ� load�� �ȵǴ� MCU simulator�̱� ������ 0�� ��ȯ�Ǿ�� ��
		System.out.println("���� Process�� ����ϴ� �޸� size: "+ Tsize);	
		return 0;
   }
	
}
