package ro.lrg.winebar;

public class WaiterTray {
	private Wine _wine;
	private Glass _glass;
	
	public void setBoth(Wine wineB, Glass glassB) {
		_wine = wineB;
		_glass = glassB;
	}
	
	public void setWine(Wine wineP) {
		_wine = wineP;
	}
	
	public void setGlass(Glass glassP) {
		_glass = glassP;
	}
		
}
