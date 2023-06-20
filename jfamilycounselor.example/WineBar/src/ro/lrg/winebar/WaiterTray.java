package ro.lrg.winebar;
class WaiterTray {
	private Wine _wine;
	private WineGlass _glass;
	public void setWine(Wine wineP) {
		_wine = wineP;
	}
	public void setGlass(WineGlass glassP) {
		_glass = glassP;
	}
	public void serve() {
		_wine.pourInto(_glass);
	}
}