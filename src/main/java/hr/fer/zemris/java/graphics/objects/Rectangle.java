package hr.fer.zemris.java.graphics.objects;

public class Rectangle extends GraphicalObject {
	private int x;
	private int y;
	private int w;
	private int h;

	public Rectangle(int x, int y, int w, int h) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public void accept(IGraphicalObjectVisitor visitor) {
		visitor.visitRectangle(this);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

}
