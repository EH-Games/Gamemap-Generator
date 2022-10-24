package gamemap;

public abstract class TileWorld implements World {
	public final Type getType() {
		return Type.TileBased;
	}
	
	public abstract int getTilesX();
	
	public abstract int getTilesY();
	
	public abstract int getTileSize();
}