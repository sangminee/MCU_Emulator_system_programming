
# MCU(Micro Controller Unit, 마이크로 컨트롤러)
: 마이크로프로세서와 입 · 출력 모듈을 하나의 칩으로 만들어져 정해진 기능을 수행하는 컴퓨터이다.  MCU는 임베디드 애플리케이션을 위해 디자인되었으며 임베디드 시스템에 널
리 사용된다. MCU는 기능을 설정하고 정해진 일을 수행하도록 프로그래밍되어 장치 등에 장
착되어 동작한다.

- 마이크로세서: 컴퓨터의 산술논리연산기, 레지스터, 프로그램 카운터, 명령디코더, 제어회
로 등의 연산장치와 제어장치를 1개의 작은 실리콘 칩에 모아놓은 처리장치를 말한다. 주기억
장치에 저장되어 있는 명령을 해석하고 실행하는 기능을 한다.2

- 구성 : CPU(Register, CU, ALU)/ Memory/ Loader(OS)

<!--  # MCU Emulator 구현
컴파일러는 Source Code를 총 2번 읽는다. 일단 첫 번째 컴파일러는 정의된 것만을 읽어서
Symbol Table을 만든다. 정의란 실행되는 코드가 아닌 실행되는 코드를 지원하기 위한 것이
다. 두 번째 컴파일러는 CPU가 읽을 수 있는 instruction Source Code를 exe로 만들어야
한다. 이때 이 exe의 header에는 Data size Segment와 Code Segment size가 들어간다. 
<br><br>
바이너리코드는 OS에 따라 달라진다. 따라서 우리가 만든 exe 코드의 header부분을 작성하
는 것도 loader 즉 OS가 하는 것이고, 실제로 메모리에 올라온 코드를 어떻게 올릴지도
Loader가 결정하는데 결국 OS를 어떻게 만듦에 따라서 달라진다는 말이다. 
<br><br>
메모리에 exe를 어떻게 쓰느냐는 100% OS(source Code를 OS의 loader가 exe로 변환해
주기 때문)가 관여를 하지만, 실제로 우리가 설계한 instruction set은 하드웨어에 종속적으로
만들어 졌다. 결국 CPU는 메모리 등과 다 연결되어 있어야 되기 때문에 CPU만 디자인 하면
안딘다. 즉 CPU만 가지고는 아무것도 하지 못한다는 뜻이다. 따라서 이것을 MCU라고 한다.  -->

# CPU 구조
-	ALU : 산술/논리 연산 담당 (AC 값을 가지고)
-	CU : 제어 담당 -> PC 값을 변경시킴
-	Register : MAR/MBR/AC/IR(opcode, operand_address)/SR/PC/SP

※ 하드웨어를 설계하는 사람들은 opcode부터 설계함

#	Execute cycle
-	fetch : exe instruction의 실행 -> memory -> IR (만약 값이 상수라면 바로 execute로)
-	decode : IR address -> AC or MBR (상대주소를 실제 주소로 바꿔 값을 가져옴)
-	execute -> ALU/CU/Memory(sta,load) 중에 결정하는 것

#	Exe 실행 과정 
1)	Compiler 
-	Source code-> exe(storage에 저장됨)
<br> ※ 2번의 과정거침(symbol table, 나머지 instruction 기계어로 변환)
2)	 Loader (OS에 존재하는 일부 프로그램)
-	Exe -> process 
<br> ※ Exe의 header(컴파일러가 생성)와 Process의 header(OS가 생성)은 다르다.  



<!-- # review
1) ALU: 인스트럭션의 순서를 결정/ 인스트럭션 자체를 실행
 => AC, status register과 연결되어 있음
2) CU: 인스트럭션의 순서를 바꿔주는 역할
 => PC, status, IR register과 연결되어 있음
 => PC값을 변경시켜줌
3) Memory: MAR, MBR register과 연결되어 동작 -->


# SP (System Programming) instruction set
: MCU Emulator를 만들 땐 우선적으로 instruction set을 만들어야 한다. 다음과 같이 CPU의 명령어인 기계어를 설계할 땐 아래와 같은 표현력을 갖게 만들어야 한다. 이렇게 기계어를만드는 것이 하드웨어를 만드는 요구사항이다. 

![image](https://user-images.githubusercontent.com/81500474/169679464-61ffa626-c177-4036-9787-baae88ee26b3.png)
