package generator.symbol;

public class Bucket {
	
	/*-----------------------------------------------------
	 *------------------ ATRIBUTOS -----------------------
	---------------------------------------------------- */
	
	private String name;
	
	private Line lines;
	
	private int memoryLocation;
	
	private Bucket next = null;
	
	/*-----------------------------------------------------
	 *------------------ CONSTRUTOR -----------------------
	---------------------------------------------------- */
	
	public Bucket( String name, int memoryLocation, Line lines ){
		
		this.name = name;
		this.memoryLocation = memoryLocation;
		this.lines = lines;
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	public String getName(){
		
		return this.name;
	}
	
	public Bucket getNext(){
		
		return this.next;
	}
	
	public int getLocation(){
		
		return this.memoryLocation;
	}
	
	public Line getLines(){
		
		return this.lines;
	}
	
	public void setNext( Bucket next ){
		
		this.next = next;
	}

}