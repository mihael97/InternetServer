package hr.fer.zemris.java.graphics.objects;

public class ObjectsGroup extends GraphicalObject {
	@Override
	public void accept(IGraphicalObjectVisitor visitor) {
		visitor.visitObjectsGroup(this);
	}
}
