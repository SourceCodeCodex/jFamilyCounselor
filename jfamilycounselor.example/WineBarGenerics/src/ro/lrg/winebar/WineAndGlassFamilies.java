package ro.lrg.winebar;
abstract class WineGlass {}
class RedWineGlass extends WineGlass {}
class WhiteWineGlass extends WineGlass {}

abstract class Wine {
    abstract void pourInto(WineGlass glass); 
}
class RedWine extends Wine {
    public void pourInto(WineGlass glass) {
        RedWineGlass redWineGlass 
        	= (RedWineGlass) glass;
        //...
}}
class WhiteWine extends Wine {
    public void pourInto(WineGlass glass) {
        WhiteWineGlass whiteWineGlass 
        	= (WhiteWineGlass)glass;	
        //...
}}