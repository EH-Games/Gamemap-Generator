package gamemap;

public interface World {
	public enum Type {
		TileBased, ModelBased;
	}
	
	public Type getType();
	
	public void render();
}