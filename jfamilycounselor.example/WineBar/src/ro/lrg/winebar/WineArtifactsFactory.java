package ro.lrg.winebar;
interface WineArtifactsFactory {
	public Wine createWine();
	public WineGlass createWineGlass();
}
class RedWineArtifactsFactory implements WineArtifactsFactory {
	public RedWine createWine() { return new RedWine(); }
	public RedWineGlass createWineGlass() { return new RedWineGlass();}
}
class WhiteWineArtifactsFactory implements WineArtifactsFactory {
	public WhiteWine createWine() { return new WhiteWine(); }
	public WhiteWineGlass createWineGlass() { return new WhiteWineGlass(); }
}