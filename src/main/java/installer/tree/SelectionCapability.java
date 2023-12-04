package installer.tree;

public enum SelectionCapability {
	
	DEFAULT (0),   // user-selectable
	PARENT  (1),   // same selection as parent (i.e. for mandatory file)
	ALWAYS  (2),   // always selected
	NEVER   (3);   // cannot be selected
	
	SelectionCapability(int strength){
		this.strength = strength;
	}
	
	/* a selection capability can never be overwritten by
	 * a weaker selection capability */
	public final int strength;

}
