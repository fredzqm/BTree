
public abstract class Identifiable implements Comparable<Identifiable>{
	
	public abstract int getIdentifier();
	
	public int compareToIdentifier(int identifier){
		return getIdentifier() - identifier;
	}
	
	public int compareTo(Identifiable o){
		return getIdentifier() - o.getIdentifier();
	}
		
}
