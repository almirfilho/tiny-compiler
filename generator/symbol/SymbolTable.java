package generator.symbol;

import java.util.Hashtable;

public class SymbolTable {
	
	/*-----------------------------------------------------
	 *------------------ ATRIBUTOS -----------------------
	---------------------------------------------------- */
	
	private Hashtable< String, Bucket > table;

	/*-----------------------------------------------------
	 *------------------ CONSTRUTOR -----------------------
	---------------------------------------------------- */

	public SymbolTable(){
		
		this.table = new Hashtable< String, Bucket >();
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	public int StLookup( String name ){
		
		Bucket bucket = ( Bucket )this.table.get( name );
		
		while( bucket != null && !name.equals( bucket.getName() ) )
			bucket = bucket.getNext();

		if( bucket == null )
			return -1;

		return bucket.getLocation();
	}
	
	public void StInsert( String name, int lineNumber, int loc ){
		
		Bucket bucket = ( Bucket )this.table.get( name );
		
		while( bucket != null && !name.equals( bucket.getName() ) )
			bucket = bucket.getNext();
		
		if( bucket == null ){ // variavel ainda nao esta na tabela

			bucket = new Bucket( name, loc, new Line( lineNumber ) );
			bucket.setNext( ( Bucket )this.table.get( name ) );
			this.table.put( name, bucket );
			
		} else { // ja esta na tabela, entao adiciona linha
			
			Line line = bucket.getLines();
			
			while( line.getNext() != null )
				line = line.getNext();
			
			line.setNext( new Line( lineNumber ) );
		}
	}
	
}
