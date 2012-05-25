package generator;

import java.io.FileWriter;
import java.io.IOException;
import generator.symbol.SymbolTable;
import analyzer.SyntaxTree;

public class CodeGenerator {
	
	/*-----------------------------------------------------
	 *------------------ ATRIBUTOS ------------------------
	---------------------------------------------------- */

	private SyntaxTree tree;
	
	private SymbolTable symbolTable;
	
	private String code;
	
	private int currentInstruction = 0;
	
	private int hightestInstruction = 0;
	
	private int tempMemoryOffset = 0;
	
	private static final int globalPointer = 5;
	
	private static final int memoryPointer = 6;
	
	private static final int accumulator = 0;
	
	private static final int accumulator2 = 1;
	
	private static final int programCounter = 7;
	
	/*-----------------------------------------------------
	 *------------------ CONSTRUTOR -----------------------
	---------------------------------------------------- */
	
	public CodeGenerator( SyntaxTree tree, SymbolTable table ){
		
		this.tree = tree;
		this.symbolTable = table;
		this.code = "";
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	public void generate( String fileName ){
		
		this.generateRM( TinyMachineCode.LD, memoryPointer, 0, accumulator );
		this.generateRM( TinyMachineCode.ST, accumulator, 0, accumulator );
		this.generateCode( this.tree );
		this.generateRO( TinyMachineCode.HALT, 0, 0, 0);
		
		FileWriter file; 
		
		try {
			
			file = new FileWriter( fileName );
			file.write( this.code );
			file.close();
			
		} catch( IOException e ){
			
			System.out.println( "Não foi possível criar arquivo de saída." );
			System.exit( -1 );
		}
	}
	
	private void generateCode( SyntaxTree tree ){
		
		if( tree != null ){
			
			if( tree.isStatement() )
				this.generateStatement( tree );
			else
				this.generateExpression( tree );
			
			this.generateCode( tree.next );
		}
	}
	
	private void generateStatement( SyntaxTree tree ){
		
		int saved1, saved2, current, loc;
		
		switch( tree.getStatementKind() ){
		
			case IF:
				
				//	parte do teste
				this.generateCode( tree.getChild( 0 ) );
				saved1 = this.skip( 1 );
				
				//	parte THEN
				this.generateCode( tree.getChild( 1 ) );
				saved2 = this.skip( 1 );
				current = this.skip( 0 );
				this.back( saved1 );
				this.generateRMAbsolute( TinyMachineCode.JEQ, accumulator, current );
				this.restore();
				
				//	parte ELSE
				this.generateCode( tree.getChild( 2 ) );
				current = this.skip( 0 );
				this.back( saved2 );
				this.generateRMAbsolute( TinyMachineCode.LDA, programCounter, current );
				this.restore();
				
				break;
				
			case REPEAT:
				
				saved1 = this.skip( 0 );				
				
				//	parte do corpo
				this.generateCode( tree.getChild( 0 ) );
				
				//	parte do teste UNTIL
				this.generateCode( tree.getChild( 1 ) );
				this.generateRMAbsolute( TinyMachineCode.JEQ, accumulator, saved1 );
				
				break;
				
			case ATTRIBUTION:
				
				this.generateCode( tree.getChild( 0 ) );
				loc = this.symbolTable.StLookup( tree.getName() );
				this.generateRM( TinyMachineCode.ST, accumulator, loc, globalPointer );
				
				break;
				
			case READ:
				
				this.generateRO( TinyMachineCode.IN, accumulator, 0, 0 );
				loc = this.symbolTable.StLookup( tree.getName() );
				this.generateRM( TinyMachineCode.ST, accumulator, loc, globalPointer );
				
				break;
				
			case WRITE:
				
				this.generateCode( tree.getChild( 0 ) );
				this.generateRO( TinyMachineCode.OUT, accumulator, 0, 0 );
				
				break;
		}
	}
	
	private void generateExpression( SyntaxTree tree ){
		
		int loc;
		
		switch( tree.getExpressionKind() ){
		
			case CONSTANT:
				this.generateRM( TinyMachineCode.LDC, accumulator, Integer.parseInt( tree.getName() ), 0 );
				break;
				
			case IDENTIFIER:
				loc = this.symbolTable.StLookup( tree.getName() );
				this.generateRM( TinyMachineCode.LD, accumulator, loc, globalPointer );
				break;
				
			case OPERATION:
				
				this.generateCode( tree.getChild( 0 ) );
				this.generateRM( TinyMachineCode.ST, accumulator, this.tempMemoryOffset--, memoryPointer );
				
				this.generateCode( tree.getChild( 1 ) );
				this.generateRM( TinyMachineCode.LD, accumulator2, ++this.tempMemoryOffset, memoryPointer );
				
				switch( tree.getTokenType() ){
				
					case PLUS:
						this.generateRO( TinyMachineCode.ADD, accumulator, accumulator2, accumulator );
						break;
						
					case MINUS:
						this.generateRO( TinyMachineCode.SUB, accumulator, accumulator2, accumulator );
						break;
						
					case MULTIPLICATION:
						this.generateRO( TinyMachineCode.MUL, accumulator, accumulator2, accumulator );
						break;
						
					case DIVISION:
						this.generateRO( TinyMachineCode.DIV, accumulator, accumulator2, accumulator );
						break;
						
					case LESS_THAN:
						this.generateRO( TinyMachineCode.SUB, accumulator, accumulator2, accumulator );
						this.generateRM( TinyMachineCode.JLT, accumulator, 2, programCounter );
						this.generateRM( TinyMachineCode.LDC, accumulator, 0, accumulator );
						this.generateRM( TinyMachineCode.LDA, programCounter, 1, programCounter );
						this.generateRM( TinyMachineCode.LDC, accumulator, 1, accumulator );
						break;
						
					case EQUALS:
						this.generateRO( TinyMachineCode.SUB, accumulator, accumulator2, accumulator );
						this.generateRM( TinyMachineCode.JEQ, accumulator, 2, programCounter );
						this.generateRM( TinyMachineCode.LDC, accumulator, 0, accumulator );
						this.generateRM( TinyMachineCode.LDA, programCounter, 1, programCounter );
						this.generateRM( TinyMachineCode.LDC, accumulator, 1, accumulator );
						break;
				}
				
				break;
		}
	}
	
	private void generateRO( String operation, int register, int offset, int baseRegister ){
		
		String tab = this.tab();
		if( operation == TinyMachineCode.HALT )
			tab = "\t";
		
		String str = this.currentInstruction++ + ":" + this.tab() + operation + tab + register + "," + offset + "," + baseRegister;
		this.test();
		this.code += this.line( str );
	}
	
	private void generateRM( String operation, int register, int offset, int baseRegister ){
		
		String str = this.currentInstruction++ + ":" + this.tab() + operation + this.tab() + register + "," + offset + "(" + baseRegister + ")";
		this.test();
		this.code += this.line( str );
	}
	
	private void generateRMAbsolute( String operation, int register, int registerAbs ){
		
		String str = this.currentInstruction + ":" + this.tab() + operation + this.tab() + register + "," + (registerAbs-(this.currentInstruction+1)) + "(" + programCounter + ")";
		++this.currentInstruction;
		this.test();
		this.code += this.line( str );
	}
	
	private String tab(){
		
		return "\t\t";
	}
	
	private String line( String string ){
		
		return string + "\n";
	}
	
	private int skip( int num ){
		
		int skip = this.currentInstruction;
		this.currentInstruction += num;
		this.test();
		
		return skip;
	}
	
	private void back( int num ){
		
		this.currentInstruction = num;
	}
	
	private void restore(){
		
		this.currentInstruction = this.hightestInstruction;
	}
	
	private void test(){
		
		if( this.hightestInstruction < this.currentInstruction )
			this.hightestInstruction = this.currentInstruction;
	}
	
	public String toString(){
		
		return this.code;
	}
	
}
