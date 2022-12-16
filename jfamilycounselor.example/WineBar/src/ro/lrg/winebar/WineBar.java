package ro.lrg.winebar;

class WineBar {
	private void doServe(Wine w, Glass g) {
		WaiterTray wt1 = new WaiterTray();
		wt1.setWine(new RedWine());
		wt1.setGlass(new RedWineGlass());

		WaiterTray wt2 = new WaiterTray();
		Wine tmp = w;
		wt2.setWine(tmp);
		wt2.setGlass(g);
		
	}
	
	public void serve() {
		doServe(new WhiteWine(), new WhiteWineGlass());
	}
}