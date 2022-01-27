public class CPU {  

	// 1. 정의 (declaration)
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
		// zero: A-B = 0 -> JMP 일어나지 X
		public boolean isZero(Register sr) {  
			if ((short) (sr.getValue() & 0x8000) == 0) {
				return false;
			} else {
				return true; 
			}
		}

		// bitwise 결과가 음수일때 false (A -B < 0 ) -> JMP 일어나지 X
		public boolean isBZ(Register sr) {  
			if ((short) (sr.getValue() & 0x4000) == 0) {
				return false;
			} else {
				return true;
			}
		}

		// bitwise 결과가 양수일때 false
		public boolean isPositive(Register sr) {
			System.out.println("SR 값: " + sr.getValue());
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
			 System.out.println("이전 AC에 있던 값:" + Integer.toHexString(this.storeValue));
			 System.out.println("현재 AC에 있는 값:" + Integer.toHexString(AC));
			 
			 short result = (short) (this.storeValue + AC);
			 registers[ERegister.eAC.ordinal()].setValue(result);
			 aluResult(result);
			 registers[ERegister.eSR.ordinal()].setValue(result);
		 }

		 public void substract(short AC) {
			 System.out.println("이전 AC에 있던 값:" + Integer.toHexString(this.storeValue));
			 System.out.println("현재 AC에 있는 값:" + Integer.toHexString(AC));
			 short result = (short) (this.storeValue - AC);
			 registers[ERegister.eAC.ordinal()].setValue(result);
			 aluResult(result);
			 registers[ERegister.eSR.ordinal()].setValue(result);  // 이게 맞나? 
		 }

		 public void mul(short AC) {
			 System.out.println("이전 AC에 있던 값:" + Integer.toHexString(this.storeValue));
			 System.out.println("현재 AC에 있는 값:" + Integer.toHexString(AC));
			 short result = (short) (this.storeValue * AC);
			 registers[ERegister.eAC.ordinal()].setValue(result);
			 aluResult(result);
			 registers[ERegister.eSR.ordinal()].setValue(result);
		 }

		 public void div(short AC) {
			 System.out.println("이전 AC에 있던 값:" + Integer.toHexString(this.storeValue));
			 System.out.println("현재 AC에 있는 값:" + Integer.toHexString(AC));
			 
			 short result = (short) (this.storeValue / AC);   // AC의 값이 없는 경우가 존재하기 때문에 오류가 발생
			 registers[ERegister.eAC.ordinal()].setValue(result);
			 aluResult(result);			
		 }
	}
	
	// 추가 함수
	private void aluResult(short result) {
		this.registers[ERegister.eIR.ordinal()].setValue(result);
		System.out.println("ALU 연산 결과 값: " +((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
	}

	private enum ERegister{  
		ePC,  // PC: 다음에 실행할 명령어의 주소 보관
		eSP,  // SP: data Segment의 시작주소 ( data segment는 코드 세그먼트 다음에 위치함 )
		eAC,  // AC: 데이터를 일시적으로 저장하는 레지스터
		eIR,
		eSR,     
		eMAR,     
		eMBR		
	}
	
	private class Register{
		protected short value;     // 기본적으로 short이다. (16bit 데이터 머신 )
	    private short getValue() {return this.value;}
	    public void setValue(short value) {this.value = value;}    
	}
	
	class IR extends Register{        // IR: 현재 실행하는 명령어를 보관하는 레지스터  => 특수한 레지스터
		public short getOpCode() { return (short) (this.value >> 8); }
		public short getOperand() { return (short) (this.value & 0x00FF ); }  
	}
	
	// components (구성요소) - 실제 CPU 구성요소, 여기서 부터 제대로 만들기 시작
	private CU cu;
	private ALU alu;
	private Register registers[];    // array  

	// associations(연관관계)
	private Memory memory;

   // status - 전기가 켜졌는지 안켜졌는지 확인
	private boolean bPowerOn;    
	private boolean isPowerOn() {return this.bPowerOn;} 

	public void setPowerOn() { // 전기 키는 method
		this.bPowerOn = true; // 전기가 켜지면	
		this.run(); 
	} 

	public void shutDown() {this.bPowerOn = false; } // 전기 끄는 method
	
	// instructions 
	      private void Halt() {
	    	   this.bPowerOn = false;
			   this.shutDown();
	      } 
		  private void LDC() {  
				// Constant의 operand -> AC
				// decode : operand 값을 AC까지로 옮기는 것
				// IR.operand -> MBR : 메모리에서 load하면 무조건 MBR로 가지고 와야 한다.
				this.registers[ERegister.eMBR.ordinal()]
						.setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
				// MBR -> AC
				this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
			}

		// 메모리의 data segment에서 값을 가지고 올 때 임의로 정한 주소에 SP값을 더해줘야 한다. 
			private void LDA() {
				// [Address]의 operand (메모리에서 값을 가지고 와야 함) -> AC   
				// decode: operand를 MBR까지 가지고 오는 것
				
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
			
			// if(false) -> 실행x -> PC값 증가해 JMP부분에서 종료
			// if(true) -> 다음 if문으로 이동
			private void JMPZ() {			
				if (this.cu.isZero( this.registers[ERegister.eSR.ordinal()])) { // ALU는 AC를 가지고, CU는 SR을 가지고 계산
					// ir.operand -> PC  (IR에 PC값이 들어가 있다.) 				
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
		this.registers = new Register[ERegister.values().length];      // n개의 array생성한 것

		for (ERegister eRegister: ERegister.values()) {
			this.registers[eRegister.ordinal()] = new Register();
		}
		
		this.registers[ERegister.eIR.ordinal()] = new IR();
	}

	public void associate1(Memory memory) {   // 외부와 연결된 메모리를 연결시켜야 함
		this.memory = memory;	
	}

	private void fetch() { 		
		// fetch : PC의 인스트럭션 값을 IR로 가지고 오는 것 -> instruction을 load
		// ordinal(): 해당 상수의 index 값을 출력  
		// getValue(): 값 반환  
		// setValue(): 값 변경						
		
		// PC -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue( this.registers[ERegister.ePC.ordinal()].getValue() );
		// memory.load(MBR) 
		this.registers[ERegister.eMBR.ordinal()].setValue(
				this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()) );
		// MBR -> IR 
		this.registers[ERegister.eIR.ordinal()].setValue( this.registers[ERegister.eMBR.ordinal()].getValue() );
		
	}	

	private void decode() { 
		// decode : operand에 있는 주소를 가져와서 데이터를 가져와 AC에다가 값을 저장
		// operator가 무엇이냐에 따라서 decode가 달라진다.	
	}

	private void execute() { 
		// 레지스터 중에서 IR을 찾아서, 이 IR의 OpCode값을 끌어낸 것!
		switch (EOpCode.values()[((IR) this.registers[ERegister.eIR.ordinal()]).getOpCode()]) {  // values() : Enum 클래스가 갖고 있는 모든 상수 값을 배열의 형태로 리턴
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
			System.out.println("해당 OpCode가 존재하지 않습니다. ");
			break;
		}
	}


	// 4) interrupt
    private void checkInterrupt() {
    	// 인터룹트: IO디바이스가 버퍼에 올리면 CPU가 확인하는 방식 (매번 확인)
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
		System.out.println(" PC값: " + pc);
		this.registers[ERegister.ePC.ordinal()].setValue(pc);	
	}

	public void setSP(short sp) {
		System.out.println(" SP값: " + sp);
		this.registers[ERegister.eSP.ordinal()].setValue(sp);	
	}

}
