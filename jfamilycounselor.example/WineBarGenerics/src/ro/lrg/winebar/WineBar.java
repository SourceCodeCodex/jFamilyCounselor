package ro.lrg.winebar;
class WineBar {
	private RedWineArtifactsFactory f1 = new RedWineArtifactsFactory();
	private WhiteWineArtifactsFactory f2 = new WhiteWineArtifactsFactory();
	private WaiterTray<RedWine,RedWineGlass> prepare(RedWine w, RedWineGlass g) {
		WaiterTray<RedWine,RedWineGlass> tray = new WaiterTray<RedWine,RedWineGlass>();
		RedWine tmp1 = w;
		RedWineGlass tmp2 = g; 
		tray.setWine(tmp1);
		tray.setGlass(tmp2);
		return tray;
	}
	private WaiterTray<WhiteWine,WhiteWineGlass> prepare(WhiteWine w, WhiteWineGlass g) {
		WaiterTray<WhiteWine,WhiteWineGlass> tray = new WaiterTray<WhiteWine,WhiteWineGlass>();
		WhiteWine tmp1 = w;
		WhiteWineGlass tmp2 = g; 
		tray.setWine(tmp1);
		tray.setGlass(tmp2);
		return tray;
	}
	public void serve() {
		WaiterTray<? extends Wine, ? extends WineGlass> tray;
		if (Math.random() > 0.5)
			tray = prepare(f1.createWine(), f1.createWineGlass());
		else 
			tray = prepare(f2.createWine(), f2.createWineGlass());
		tray.serve();
	}
}