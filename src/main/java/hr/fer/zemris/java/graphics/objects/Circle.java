package hr.fer.zemris.java.graphics.objects;

public class Circle extends GraphicalObject {
	private int x;
	private int y;
	private int r;

	public Circle(int x, int y, int r) {
		super();
		this.x = x;
		this.y = y;
		this.r = r;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getR() {
		return r;
	}

	@Override
	public void accept(IGraphicalObjectVisitor visitor) {
		visitor.visitCircle(this);
	}
}
