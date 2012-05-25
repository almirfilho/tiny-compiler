package generator.symbol;

import analyzer.SyntaxTree;
import analyzer.SyntaxType;
import analyzer.TokenType;

public class TableBuilder {

	/*-----------------------------------------------------
	 *------------------ ATRIBUTOS ------------------------
	---------------------------------------------------- */
	
	private SyntaxTree tree;
	
	private SymbolTable table;
	
	public int location = 0;
	
	private abstract class TraverseProcedure {
		
		public void run( SyntaxTree tree ){}
	}
	
	private class Null extends TraverseProcedure {
		
		public void run( SyntaxTree tree ){
			
			if( tree == null ) return;
			return;
		}
	}
	
	private class InsertNode extends TraverseProcedure {
		
		public void run( SyntaxTree tree ){
						
			if( tree.isStatement() ){
				
				switch( tree.getStatementKind() ){
				
					case ATTRIBUTION:
					case READ:
						
						if( table.StLookup( tree.getName() ) == -1 )
							table.StInsert( tree.getName(), tree.getLine(), location++ );
						else
							table.StInsert( tree.getName(), tree.getLine(), 0 );

						break;
				}
				
				
			} else {
				
				if( tree.getExpressionKind() == SyntaxType.ExpressionKind.IDENTIFIER ){
					
					if( table.StLookup( tree.getName() ) == -1 )
						table.StInsert( tree.getName(), tree.getLine(), location++ );
					else
						table.StInsert( tree.getName(), tree.getLine(), 0 );
				}
			}
		}
	}
	
	private class CheckNode extends TraverseProcedure {
		
		public void run( SyntaxTree tree ){
			
			if( tree.isStatement() ){
				
				switch( tree.getStatementKind() ){
				
					case IF:
						
						if( tree.getChild( 0 ).getExpressionType() == SyntaxType.ExpressionType.INTEGER )
							System.out.println( "erro" );
						
						break;
						
					case ATTRIBUTION:
						
						if( tree.getChild( 0 ).getExpressionType() != SyntaxType.ExpressionType.INTEGER )
							System.out.println( "erro" );
						
						break;
						
					case WRITE:
						
						if( tree.getChild( 0 ).getExpressionType() != SyntaxType.ExpressionType.INTEGER )
							System.out.println( "erro" );
						
						break;
						
					case REPEAT:
						
						if( tree.getChild( 1 ).getExpressionType() == SyntaxType.ExpressionType.INTEGER )
							System.out.println( "erro" );
						
						break;
				}
				
			} else {
				
				switch( tree.getExpressionKind() ){
				
					case OPERATION:
						
						if( tree.getChild( 0 ).getExpressionType() != SyntaxType.ExpressionType.INTEGER ||
							tree.getChild( 1 ).getExpressionType() != SyntaxType.ExpressionType.INTEGER )
							System.out.println( "erro" );
						
						if( tree.getTokenType() == TokenType.Element.EQUALS ||
							tree.getTokenType() == TokenType.Element.LESS_THAN )
							tree.setExpressionType( SyntaxType.ExpressionType.BOOLEAN );
						else
							tree.setExpressionType( SyntaxType.ExpressionType.INTEGER );
						
						break;
					
					case CONSTANT:
					case IDENTIFIER:
						tree.setExpressionType( SyntaxType.ExpressionType.INTEGER );
						break;
				}
			}
		}
	}
	
	/*-----------------------------------------------------
	 *------------------ CONSTRUTOR -----------------------
	---------------------------------------------------- */
	
	public TableBuilder( SyntaxTree tree ){
		
		this.tree = tree;
		this.table = new SymbolTable();
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	public SymbolTable build(){
		
		this.traverse( this.tree, new InsertNode(), new Null() );
		this.typeCheck();
		return this.table;
	}
	
	private void traverse( SyntaxTree tree, TraverseProcedure pre, TraverseProcedure pos ){
		
		if( tree == null ) return;

		pre.run( tree );

		for( SyntaxTree child : tree.getChildren() )
			this.traverse( child, pre, pos );
		
		pos.run( tree );
		this.traverse( tree.next, pre, pos );
	}
	
	private void typeCheck(){
		
		this.traverse( this.tree, new Null(), new CheckNode() );
	}
	
}