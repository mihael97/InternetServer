package hr.fer.zemris.java.graphics.objects;

public class CalcAreaVisitor implements IGraphicalObjectVisitor {

	private double area = 0;

	@Override
	public void visitObjectsGroup(ObjectsGroup object) {
		for (int index = object.numberOfChildren() - 1; index >= 0; index--) {
			object.getChild(index).accept(this);
		}

	}

	@Override
	public void visitCircle(Circle object) {
		area += object.getR() * object.getR() * Math.PI;
	}

	@Override
	public void visitRectangle(Rectangle object) {
		area += object.getW() * object.getH();
	}

	public double getArea() {
		return area;
	}

}
