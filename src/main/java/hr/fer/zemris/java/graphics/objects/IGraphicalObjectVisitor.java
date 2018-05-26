package hr.fer.zemris.java.graphics.objects;

public interface IGraphicalObjectVisitor {
	public void visitObjectsGroup(ObjectsGroup object);

	public void visitCircle(Circle object);

	public void visitRectangle(Rectangle object);
}
