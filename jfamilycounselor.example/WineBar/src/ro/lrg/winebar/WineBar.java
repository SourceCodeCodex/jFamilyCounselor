package ro.lrg.winebar;
class WineBar {
	private RedWineArtifactsFactory f1 = new RedWineArtifactsFactory();
	private WhiteWineArtifactsFactory f2 = new WhiteWineArtifactsFactory();
	private WaiterTray prepare(Wine w, WineGlass g) {
		WaiterTray tray = new WaiterTray();
		Wine tmp1 = w;
		WineGlass tmp2 = g; 
		tray.setWine(tmp1);
		tray.setGlass(tmp2);
		return tray;
	}
	public void serve() {
		WaiterTray tray;
		if (Math.random() > 0.5)
			tray = prepare(f1.createWine(), f1.createWineGlass());
		else 
			tray = prepare(f2.createWine(), f2.createWineGlass());
		tray.serve();
	}
}