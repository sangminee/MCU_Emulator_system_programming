public class CPU {  

	// 1. ���� (declaration)
	private enum EOpCode { 
		eHalt, // 0x00
		eLDC, // 0x01
		eLDA, // 0x02	
		eSTA, // 0x03
		eADDA, // 0x04
		eADDC, // 0x05
		eSUBA, // 0x06
		eSUBC, // 0x07
		eMULA, // 0x08
		eDIVA, // 0x09
		
		eDIVC, // 0x0A
		eANDA, // 0x0B
		eNOTA, // 0x0C
		eJMPZ, // 0x0D
		eJMPBZ, // 0x0E
		eJMPFinal,  // 0x0F
		
		eJMPPositive, // 0x10
	}
	
	private class CU {
		// zero: A-B = 0 -> JMP �Ͼ�� X
		public boolean isZero(Register sr) {  
			if ((short) (sr.getValue() & 0x8000) == 0) {
				return false;
			} else {
				return true; 
			}
		}

		// bitwise ����� �����϶� false (A -B < 0 ) -> JMP �Ͼ�� X
		public boolean isBZ(Register sr) {  
			if ((short) (sr.getValue() & 0x4000) == 0) {
				return false;
			} else {
				return true;
			}
		}

		// bitwise ����� ����϶� false
		public boolean isPositive(Register sr) {
			System.out.println("SR ��: " + sr.getValue());
			if (sr.getValue() < 0) {
				return true;
			} else {
				if ((short) (sr.getValue() & 0x00FF) > 0) {
					System.out.println("bitwise And result=> " + (short) (sr.getValue() & 0x00FF));
					return false;
				} else {
					System.out.println("bitwise And result=> " + (short) (sr.getValue() & 0x00FF));
					return true;
				}
			}
		}
	}

	private class ALU{
		 private short storeValue;
		
		 public void store(short preAC) {
			 this.storeValue = preAC;
		 }
		 
		 public void add(short AC) {
			 System.out.println("���� AC�� �ִ� ��:" + Integer.toHexString(this.storeValue));
			 System.out.println("���� AC�� �ִ� ��:" + Integer.toHexString(AC));
			 
			 short result = (short) (this.storeValue + AC);
			 registers[ERegister.eAC.ordinal()].setValue(result);
			 aluResult(result);
			 registers[ERegister.eSR.ordinal()].setValue(result);
		 }

		 public void substract(short AC) {
			 System.out.println("���� AC�� �ִ� ��:" + Integer.toHexString(this.storeValue));
			 System.out.println("���� AC�� �ִ� ��:" + Integer.toHexString(AC));
			 short result = (short) (this.storeValue - AC);
			 registers[ERegister.eAC.ordinal()].setValue(result);
			 aluResult(result);
			 registers[ERegister.eSR.ordinal()].setValue(result);  // �̰� �³�? 
		 }

		 public void mul(short AC) {
			 System.out.println("���� AC�� �ִ� ��:" + Integer.toHexString(this.storeValue));
			 System.out.println("���� AC�� �ִ� ��:" + Integer.toHexString(AC));
			 short result = (short) (this.storeValue * AC);
			 registers[ERegister.eAC.ordinal()].setValue(result);
			 aluResult(result);
			 registers[ERegister.eSR.ordinal()].setValue(result);
		 }

		 public void div(short AC) {
			 System.out.println("���� AC�� �ִ� ��:" + Integer.toHexString(this.storeValue));
			 System.out.println("���� AC�� �ִ� ��:" + Integer.toHexString(AC));
			 
			 short result = (short) (this.storeValue / AC);   // AC�� ���� ���� ��찡 �����ϱ� ������ ������ �߻�
			 registers[ERegister.eAC.ordinal()].setValue(result);
			 aluResult(result);			
		 }
	}
	
	// �߰� �Լ�
	private void aluResult(short result) {
		this.registers[ERegister.eIR.ordinal()].setValue(result);
		System.out.println("ALU ���� ��� ��: " +((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
	}

	private enum ERegister{  
		ePC,  // PC: ������ ������ ��ɾ��� �ּ� ����
		eSP,  // SP: data Segment�� �����ּ� ( data segment�� �ڵ� ���׸�Ʈ ������ ��ġ�� )
		eAC,  // AC: �����͸� �Ͻ������� �����ϴ� ��������
		eIR,
		eSR,     
		eMAR,     
		eMBR		
	}
	
	private class Register{
		protected short value;     // �⺻������ short�̴�. (16bit ������ �ӽ� )
	    private short getValue() {return this.value;}
	    public void setValue(short value) {this.value = value;}    
	}
	
	class IR extends Register{        // IR: ���� �����ϴ� ��ɾ �����ϴ� ��������  => Ư���� ��������
		public short getOpCode() { return (short) (this.value >> 8); }
		public short getOperand() { return (short) (this.value & 0x00FF ); }  
	}
	
	// components (�������) - ���� CPU �������, ���⼭ ���� ����� ����� ����
	private CU cu;
	private ALU alu;
	private Register registers[];    // array  

	// associations(��������)
	private Memory memory;

   // status - ���Ⱑ �������� ���������� Ȯ��
	private boolean bPowerOn;    
	private boolean isPowerOn() {return this.bPowerOn;} 

	public void setPowerOn() { // ���� Ű�� method
		this.bPowerOn = true; // ���Ⱑ ������	
		this.run(); 
	} 

	public void shutDown() {this.bPowerOn = false; } // ���� ���� method
	
	// instructions 
	      private void Halt() {
	    	   this.bPowerOn = false;
			   this.shutDown();
	      } 
		  private void LDC() {  
				// Constant�� operand -> AC
				// decode : operand ���� AC������ �ű�� ��
				// IR.operand -> MBR : �޸𸮿��� load�ϸ� ������ MBR�� ������ �;� �Ѵ�.
				this.registers[ERegister.eMBR.ordinal()]
						.setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
				// MBR -> AC
				this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
			}

		// �޸��� data segment���� ���� ������ �� �� ���Ƿ� ���� �ּҿ� SP���� ������� �Ѵ�. 
			private void LDA() {
				// [Address]�� operand (�޸𸮿��� ���� ������ �;� ��) -> AC   
				// decode: operand�� MBR���� ������ ���� ��
				
				// IR.operand -> MAR
				this.registers[ERegister.eMAR.ordinal()].setValue(
						(short) (((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand()/2
								+ this.registers[ERegister.eSP.ordinal()].getValue()));

				// memory.load(MAR) -> MBR
				this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue() ));				
				// MBR -> AC
				this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());

			}

			private void STA() {
				System.out.println("< STA >");	
				
				// IR.operand -> MAR 
				this.registers[ERegister.eMAR.ordinal()].setValue(
						(short) (((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand()/2
								+ this.registers[ERegister.eSP.ordinal()].getValue()));
				// AC -> MBR
				this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eAC.ordinal()].getValue());
				
				// memory.store(MAR,MBR)
				this.memory.store(
						this.registers[ERegister.eMAR.ordinal()].getValue(),
						this.registers[ERegister.eMBR.ordinal()].getValue());
				
				System.out.println("this.memory["+this.registers[ERegister.eMAR.ordinal()].getValue() + "]= "+ this.registers[ERegister.eMBR.ordinal()].getValue());
			}

			private void ADDA() {
				// AC -> alu
				this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());							
				// IR.operand -> MBR
				this.LDA();				
				// alu.store + MBR
				this.alu.add(this.registers[ERegister.eMBR.ordinal()].getValue());
			}

			private void ADDC() {
				// AC -> alu
				this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());				
				// IR.operand -> MBR
				this.LDC();				
				// alu.store + MBR
				this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
			}

			private void SUBA() {
				// AC -> alu
				this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
				// IR.operand -> MBR
				this.LDA();
				// alu.store - MBR
				this.alu.substract(this.registers[ERegister.eAC.ordinal()].getValue());
			}

			private void SUBC() {
				// AC -> alu
				this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
				// IR.operand -> MBR
				this.LDC();
				// alu.store - MBR
				this.alu.substract(this.registers[ERegister.eAC.ordinal()].getValue());
			}

			private void MULA() {
				// AC -> alu
				this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
				// IR.operand -> MBR
				this.LDC();
				// alu.store * MBR
				this.alu.mul(this.registers[ERegister.eAC.ordinal()].getValue());
			}

			private void DIVA() {
				// AC -> alu
				this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
				// IR.operand -> MBR
				this.LDA();
				// alu.store / MBR
				this.alu.div(this.registers[ERegister.eAC.ordinal()].getValue());
			}

			private void DIVC() {
				// AC -> alu
				this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
				// IR.operand -> MBR
				this.LDC();
				// alu.store / MBR
				this.alu.div(this.registers[ERegister.eAC.ordinal()].getValue());
			}

			private void ANDA() {
			}

			private void NOTA() {
			}
			
			// if(false) -> ����x -> PC�� ������ JMP�κп��� ����
			// if(true) -> ���� if������ �̵�
			private void JMPZ() {			
				if (this.cu.isZero( this.registers[ERegister.eSR.ordinal()])) { // ALU�� AC�� ������, CU�� SR�� ������ ���
					// ir.operand -> PC  (IR�� PC���� �� �ִ�.) 				
					this.registers[ERegister.ePC.ordinal()]
							.setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());				
				} 
			}

			private void JMPBZ() {
				if (this.cu.isBZ( this.registers[ERegister.eSR.ordinal()])) { 
					this.registers[ERegister.ePC.ordinal()]
							.setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
				}
			}

			private void JMPFinal() {
				this.registers[ERegister.ePC.ordinal()].setValue((short) (this.registers[ERegister.eSP.ordinal()].getValue() - 1));
			}
			
			private void JMPPositive() {
				if (this.cu.isPositive( this.registers[ERegister.eSR.ordinal()])) { 
					this.registers[ERegister.ePC.ordinal()]
							.setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
				}				
			}

	// constructor
	public CPU() {
		this.cu = new CU();		
		this.alu = new ALU();
		this.registers = new Register[ERegister.values().length];      // n���� array������ ��

		for (ERegister eRegister: ERegister.values()) {
			this.registers[eRegister.ordinal()] = new Register();
		}
		
		this.registers[ERegister.eIR.ordinal()] = new IR();
	}

	public void associate1(Memory memory) {   // �ܺο� ����� �޸𸮸� ������Ѿ� ��
		this.memory = memory;	
	}

	private void fetch() { 		
		// fetch : PC�� �ν�Ʈ���� ���� IR�� ������ ���� �� -> instruction�� load
		// ordinal(): �ش� ����� index ���� ���  
		// getValue(): �� ��ȯ  
		// setValue(): �� ����						
		
		// PC -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue( this.registers[ERegister.ePC.ordinal()].getValue() );
		// memory.load(MBR) 
		this.registers[ERegister.eMBR.ordinal()].setValue(
				this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()) );
		// MBR -> IR 
		this.registers[ERegister.eIR.ordinal()].setValue( this.registers[ERegister.eMBR.ordinal()].getValue() );
		
	}	

	private void decode() { 
		// decode : operand�� �ִ� �ּҸ� �����ͼ� �����͸� ������ AC���ٰ� ���� ����
		// operator�� �����̳Ŀ� ���� decode�� �޶�����.	
	}

	private void execute() { 
		// �������� �߿��� IR�� ã�Ƽ�, �� IR�� OpCode���� ��� ��!
		switch (EOpCode.values()[((IR) this.registers[ERegister.eIR.ordinal()]).getOpCode()]) {  // values() : Enum Ŭ������ ���� �ִ� ��� ��� ���� �迭�� ���·� ����
		case eHalt: 
			this.Halt();
			break;
		case eLDC:  
			this.LDC();
			break;
		case eLDA:
			this.LDA();
			break;
		case eSTA: 
			this.STA();
			break;
		case eADDA:
			this.ADDA();
			break;
		case eADDC:
			this.ADDC();
			break;
		case eSUBA: 
			this.SUBA();
			break;
		case eSUBC: 
			this.SUBC();
			break;			
		case eMULA: 
			this.MULA();
			break;			
		case eDIVA:
			this.DIVA();
			break;		
		case eDIVC:
			this.DIVC();
			break;	
		case eANDA:
			this.ANDA();
			break;			
		case eNOTA: 
			this.NOTA();
			break;			
		case eJMPZ: 
			this.JMPZ();
			break;
		case eJMPBZ: 
			this.JMPBZ();
			break;
		case eJMPFinal: 
			this.JMPFinal();
			break;
			
		case eJMPPositive: 
			this.JMPPositive();
			break;

		default:
			System.out.println("�ش� OpCode�� �������� �ʽ��ϴ�. ");
			break;
		}
	}


	// 4) interrupt
    private void checkInterrupt() {
    	// ���ͷ�Ʈ: IO����̽��� ���ۿ� �ø��� CPU�� Ȯ���ϴ� ��� (�Ź� Ȯ��)
	}

	public void run() {
		System.out.println();
		System.out.println("==================================");
		System.out.println();
		
		while (this.isPowerOn()) { 
			
			System.out.println("---------- PC: " + this.registers[ERegister.ePC.ordinal()].getValue() + " ------------");
			short a = this.registers[ERegister.ePC.ordinal()].getValue();

			// instruction lifestyle
			this.fetch();
			this.decode();
			this.execute();
			this.checkInterrupt();

			if (this.registers[ERegister.ePC.ordinal()].getValue() == a) {
				this.registers[ERegister.ePC.ordinal()]
						.setValue((short) (this.registers[ERegister.ePC.ordinal()].getValue() + 1));
			}
		}
	}

	public static void main(String args[]) {
		CPU cpu = new CPU();	
		Memory memory = new Memory();
		Loader loader = new Loader();
						
		loader.loadProcess("main", memory, cpu);
		cpu.associate1(memory);	
		
		cpu.setPowerOn();
		
	}

	public void setPC(short pc) {
		System.out.println(" PC��: " + pc);
		this.registers[ERegister.ePC.ordinal()].setValue(pc);	
	}

	public void setSP(short sp) {
		System.out.println(" SP��: " + sp);
		this.registers[ERegister.eSP.ordinal()].setValue(sp);	
	}

}
