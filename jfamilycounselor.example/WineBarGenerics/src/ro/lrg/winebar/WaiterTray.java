package ro.lrg.winebar;
class WaiterTray<W extends Wine, G extends WineGlass> {
	private W _wine;
	private G _glass;
	public void setWine(W wineP) {
		_wine = wineP;
	}
	public void setGlass(G glassP) {
		_glass = glassP;
	} 
	public void serve() {
		_wine.pourInto(_glass);
	}
}