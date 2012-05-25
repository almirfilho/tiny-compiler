package generator.symbol;

public class Line {
	
	/*-----------------------------------------------------
	 *------------------ ATRIBUTOS -----------------------
	---------------------------------------------------- */

	private int lineNumber;
	
	private Line next = null;
	
	/*-----------------------------------------------------
	 *------------------ CONSTRUTOR -----------------------
	---------------------------------------------------- */
	
	public Line( int lineNumber ){
		
		this.lineNumber = lineNumber;
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	public Line getNext(){
		
		return this.next;
	}
	
	public void setNext( Line next ){
		
		this.next = next;
	}
	
	public int getNumber(){
		
		return this.lineNumber;
	}
	
}
